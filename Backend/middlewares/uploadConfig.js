import multer from "multer";
import { CloudinaryStorage } from "multer-storage-cloudinary";
import cloudinary from "../config/cloudinary.js";

const storage = new CloudinaryStorage({
  cloudinary,
  params: {
    folder: "songs", // Cloudinary folder
    resource_type: "auto", // detects audio/video/image
    format: async (req, file) => "mp3", // optional override
  },
});

const upload = multer({ storage });

export default upload;
