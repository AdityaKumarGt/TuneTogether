// schemas/roomSchema.js
import mongoose from "mongoose";
import validator from "validator";

const roomSchema = new mongoose.Schema(
  {
     roomName: {
      type: String,
      required: true,
    },
      hostName: {
      type: String,
      required: true,
    },
    hostId: {
      type: String,
      required: true,
    },
    hostEmail: {
      type: String,
      required: true,
    },
    hostFcmToken: {
      type: String,
      required: true,
    },
    songs: [
      {
        name: String,
        url: String,
        // duration: Number, // in milliseconds
      },
    ],
    members: [
      {
        name:{
          type: String,
          required: true,
        },
        email: {
          type: String,
          required: true,
          validate: [validator.isEmail, "Please Provide A Valid Email!"],
        },
        fcmToken: {
          type: String,
          required: true,
        },
      },
    ],
    startTime: {
      type: Date,
      default: null,
    },
    // status: {
    //   type: String,
    //   enum: ["waiting", "started", "ended"],
    //   default: "waiting",
    // },
  },
  { timestamps: true }
);

const Room = mongoose.model("Room", roomSchema);

export default Room;
