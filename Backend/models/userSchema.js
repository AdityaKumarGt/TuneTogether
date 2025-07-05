import mongoose from "mongoose";

const userSchema = new mongoose.Schema({
  name: { type: String, required: true },
  _id: { type: String, required: true }, // Firebase UID
  email: { type: String, required: true },
  profilePhoto: { type: String, required: true },
  fcmToken: { type: String },
  addedRooms: [
    {
      roomName: { type: String },
      roomId: { type: String },
      hostName: { type: String },
      hostEmail: { type: String },
      startTime: { type: Date },
      isCreatedByYou: {
        type: Boolean,
        default: false,
      },
      members: [
        {
          memberName: { type: String },
          memberEmail: { type: String },
        },
      ],
    },
  ],
});

// export const User = mongoose.model("User", userSchema);
export const User = mongoose.models.User || mongoose.model("User", userSchema);
