import express from "express";
import { getUserRooms, home, newUser } from "../controllers/userController.js";
 
const router = express.Router();

router.get("/", home);
router.post("/new", newUser);
router.get("/rooms", getUserRooms);

export default router;
