// websocket/websocketHandlers.js
const rooms = new Map(); // roomId -> Set of sockets

export default function setupWebSocketHandlers(wss) {
  wss.on("connection", (ws) => {
    console.log("⚡ Client connected");

    ws.on("message", (message) => {
      try {
        const { type, payload } = JSON.parse(message);

        switch (type) {
          case "join-room":
            handleJoinRoom(ws, payload.roomId);
            break;

          case "play-song":
          case "pause-song":
          case "seek-song":
            broadcastToRoom(payload.roomId, {
              type,
              payload,
            });
            break;

          default:
            console.warn("Unknown message type:", type);
        }
      } catch (err) {
        console.error("Invalid message", err);
      }
    });

    ws.on("close", () => {
      console.log("❌ Client disconnected");
      for (const room of rooms.values()) {
        room.delete(ws);
      }
    });
  });

  

}

function handleJoinRoom(ws, roomId) {
  if (!roomId) return;

  if (!rooms.has(roomId)) {
    rooms.set(roomId, new Set());
  }

  rooms.get(roomId).add(ws);
  ws.send(JSON.stringify({ type: "joined-room", payload: { roomId } }));

  console.log(`✅ Client joined room: ${roomId}`);
}

function broadcastToRoom(roomId, data) {
  const room = rooms.get(roomId);
  if (!room) return;

  for (const client of room) {
    if (client.readyState === 1) {
      client.send(JSON.stringify(data));

  console.log(`Broadcast Data: ${JSON.stringify(data)}`);

    }
  }
}





/**
// this code deletes the room, if no one is in the room::

   ws.on("close", () => {
  console.log("❌ Client disconnected");
  for (const [roomId, room] of rooms.entries()) {
    room.delete(ws);
    if (room.size === 0) {
      rooms.delete(roomId); // delete room if no one is in it
      console.log(`Room ${roomId} deleted (empty)`);
    }
  }
});
**/