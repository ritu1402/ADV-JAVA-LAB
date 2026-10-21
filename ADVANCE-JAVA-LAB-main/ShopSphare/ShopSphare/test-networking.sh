#!/usr/bin/env bash
# ==============================================================================
# ShopSphere Automated Networking Test Suite
# Tests both the Low-Level TCP Socket Module and the Java RMI Module
# ==============================================================================
set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "=================================================="
echo " 1. Compiling All Java Classes"
echo "=================================================="
javac -cp "target/classes:/home/sahil-sepat/.m2/repository/jakarta/servlet/jakarta.servlet-api/6.1.0/jakarta.servlet-api-6.1.0.jar" \
      -d target/classes \
      src/main/java/com/shopsphere/model/*.java \
      src/main/java/com/shopsphere/util/*.java \
      src/main/java/com/shopsphere/dao/*.java \
      src/main/java/com/shopsphere/service/*.java \
      src/main/java/com/shopsphere/controller/*.java \
      src/main/java/com/shopsphere/filter/*.java \
      src/main/java/com/shopsphere/network/socket/*.java \
      src/main/java/com/shopsphere/network/rmi/*.java

echo "Compilation successful!"

echo ""
echo "=================================================="
echo " 2. Testing Low-Level TCP Socket Client-Server"
echo "=================================================="
# Launch Socket Server in background on port 8092
java -cp target/classes com.shopsphere.network.socket.SocketInventoryServer 8092 &
SOCKET_PID=$!
sleep 1

# Run Socket Client
java -cp target/classes com.shopsphere.network.socket.SocketInventoryClient localhost 8092

# Terminate Socket Server
kill $SOCKET_PID 2>/dev/null || true
wait $SOCKET_PID 2>/dev/null || true

echo ""
echo "=================================================="
echo " 3. Testing Java RMI Client-Server"
echo "=================================================="
# Launch RMI Server in background
java -cp target/classes com.shopsphere.network.rmi.RmiInventoryServer &
RMI_PID=$!
sleep 1

# Run RMI Client
java -cp target/classes com.shopsphere.network.rmi.RmiInventoryClient localhost 1099

# Terminate RMI Server
kill $RMI_PID 2>/dev/null || true
wait $RMI_PID 2>/dev/null || true

echo ""
echo "=================================================="
echo " All Networking Modules Tested Successfully!"
echo "=================================================="

