import { User } from "../models/userSchema.js"; // or wherever your User model is
import { catchAsyncErrors } from "../middlewares/catchAsyncErrors.js";
import ErrorHandler from "../middlewares/error.js";
import upload from "../middlewares/uploadConfig.js";
import admin from "../utils/firebase.js"; // path to your firebase.js
import Room from "../models/roomSchema.js";

export const createRoom = [
  upload.array("songs"), // allow multiple songs

  catchAsyncErrors(async (req, res, next) => {
    const { roomName, hostName, hostId, hostEmail, hostFcmToken, startTime } =
      req.body;
    const memberEmailIds = JSON.parse(req.body.memberEmailIds || "[]");
    const songFiles = req.files;
    if (
      !roomName ||
      !hostName ||
      !hostId ||
      !hostEmail ||
      !hostFcmToken ||
      !startTime
    ) {
      return next(new ErrorHandler("Please add all fields", 400));
    }
    if (!songFiles || songFiles.length === 0) {
      return next(new ErrorHandler("No song files uploaded", 400));
    }

    // Fetch FCM tokens for invited members
    const membersFromDB = await User.find(
      { email: { $in: memberEmailIds } },
      { name: 1, email: 1, fcmToken: 1, _id: 0 }
    );

    const memberObjects = membersFromDB.map((user) => ({
      name: user.name,
      email: user.email,
      fcmToken: user.fcmToken,
    }));

    const fcmTokens = membersFromDB
      .map((user) => user.fcmToken)
      .filter(Boolean);

    const songs = songFiles.map((file) => ({
      name: file.originalname,
      url: file.path,
    }));

    // Save Room to DB
    const newRoom = await Room.create({
      roomName,
      hostName,
      hostId,
      hostEmail,
      hostFcmToken,
      songs,
      members: memberObjects,
      startTime,
      // status: "waiting",
    });

    // Update each member's addedRoomIds to include this roomId
    const memberDetails = [
      // First add the host as a member
      {
        memberName: hostName,
        memberEmail: hostEmail,
      },
      // Then add all other members
      ...membersFromDB.map((user) => ({
        memberName: user.name,
        memberEmail: user.email,
      })),
    ];

    //update members Rooms Details
    await User.updateMany(
      { email: { $in: memberEmailIds } },
      {
        $addToSet: {
          addedRooms: {
            roomName: newRoom.roomName || "Unnamed Room",
            roomId: newRoom._id.toString(),
            hostName: newRoom.hostName,
            hostEmail: newRoom.hostEmail,
            startTime: newRoom.startTime,
            isCreatedByYou: false,
            members: memberDetails,
          },
        },
      }
    );

    //update host's Room Details
    await User.updateOne(
      { email: newRoom.hostEmail },
      {
        $addToSet: {
          addedRooms: {
            roomName: newRoom.roomName || "Unnamed Room",
            roomId: newRoom._id.toString(),
            hostName: newRoom.hostName,
            hostEmail: newRoom.hostEmail,
            startTime: newRoom.startTime,
            isCreatedByYou: true,
            members: memberDetails,
          },
        },
      }
    );

    // Send notification to each token
    const message = {
      data: {
        title: "You've been invited to a room!",
        body: "A new room has been created by host",
        roomId: newRoom._id.toString(), // you can pass extra info if needed
      },
    };

    const sendResults = await Promise.allSettled(
      fcmTokens.map((token) => admin.messaging().send({ ...message, token }))
    );

    return res.status(201).json({
      success: true,
      message,
      sendResults,
      room: newRoom,
    });
  }),
];


export const getRoom = catchAsyncErrors(async (req, res, next) => {
  const {roomId } = req.query;
  const room = await Room.findById(roomId);

  if(!room){
      return next(new ErrorHandler("Room not found with this roomId", 400));
  }
  return res.status(201).json({
    success: true,
    room,
  });
});