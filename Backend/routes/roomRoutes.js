// routes/roomRoutes.js
import express from 'express';
// import { createRoom } from '../controllers/roomController.js';
import Room from '../models/roomSchema.js';
import { createRoom, getRoom } from '../controllers/roomController.js';

const router = express.Router();

//route: /api/v1/rooms/create
router.post('/create', createRoom);

//route: /api/v1/rooms/:roomId
router.get('/', getRoom);
 
 

export default router;
