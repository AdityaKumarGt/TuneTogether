import { User } from "../models/UserSchema.js";
import { catchAsyncErrors } from "../middlewares/catchAsyncErrors.js";
import ErrorHandler from "../middlewares/error.js";

export const newUser = catchAsyncErrors(async (req, res, next) => {
  const { name, email, uid, profilePhoto, fcmToken } = req.body;

  if (!uid || !name || !email || !fcmToken || !profilePhoto) {
    return next(new ErrorHandler("Please add all fields", 400));
  }
  
let user = await User.findById(uid).select("-addedRooms");
  if (user) {
    // Update the FCM token
    user.fcmToken = fcmToken;
    await user.save();

    return res.status(200).json({
      success: true,
      message: `Welcome Back, ${user.name}`,
      userDetails: user,
    });
  }

  // creation
  user = await User.create({
    name,
    email,
    _id: uid,
    profilePhoto,
    fcmToken,
  });
  return res.status(201).json({
    success: true,
    message: `Welcome, ${user.name}`,
    userDetails: user,
  });
});

export const getUserRooms = catchAsyncErrors(async (req, res, next) => {
  const {uid } = req.query;
  const user = await User.findById(uid).select("addedRooms")

  if(!user){
      return next(new ErrorHandler("User not found with this uid", 400));
  }
  return res.status(201).json({
    success: true,
    userRooms: user.addedRooms,
  });
});

export const home = catchAsyncErrors(async (req, res, next) => {
  return res.status(201).json({
    success: true,
    message: `Welcome to TuneTogether backend`,
  });
});
