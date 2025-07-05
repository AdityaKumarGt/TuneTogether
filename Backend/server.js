// server.js
import http from "http";
import app from "./app.js";
import { WebSocketServer } from "ws"; // ✅ THIS LINE IS IMPORTANT
import setupWebSocketHandlers from "./websocket/websocketHandlers.js"; // we’ll create this

const PORT = process.env.PORT || 4000;

const server = http.createServer(app);

// 🔌 Attach WebSocket server
const wss = new WebSocketServer({ server });

// 🔧 Setup connection logic
setupWebSocketHandlers(wss);

server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});

