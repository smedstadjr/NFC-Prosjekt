from bluetooth import *  # Import Bluetooth module for handling Bluetooth connections and transmitting data


def bluetooth_server(lock, unlock):
    server_sock = BluetoothSocket(RFCOMM)  # Creating a Bluetooth socket using rfcomm protocol
    server_sock.bind(("", PORT_ANY))  # Bind the socket to any available port
    server_sock.listen(1)  # Start listening for incoming connections
    port = server_sock.getsockname()[1]  # Get the assigned port number
    print(f"Bluetooth server listening on RFCOMM channel {port}")

    # Accept a connection from a Bluetooth client
    client_sock, client_info = server_sock.accept()
    print(f"Accepted connection from {client_info}")

    try:
        # Listen for data from a client, continuosly
        while True:
            print("Attempting to receive data...")  # Message before recieving data
            data = client_sock.recv(1024)  # Receive data (max 1024 bytes)
            print("Data received:", data)  # Log the data for confirmation

            # Check if no data was received (client disconnected)
            if len(data) == 0:
                break


            # Process data if it is 12 characters long
            if len(data) == 12:
                lockID = int(data[:4])  # Extract lock ID from the first 4 characters
                batteryLevel = int(data[4:8])  # Extract battery level from characters 5-8
                lockStatus = int(data[8:12])  # Extract lock status from characters 9-12
                print(f"lockID: {lockID}, batteryLevel: {batteryLevel}, lockStatus: {lockStatus}")

                # Call appropriate function based on lock status
                if lockStatus == 1:
                    lock()
                else:
                    unlock()
            else:
                # Notify if data received does not match expected length
                print(f"Received data is not 12 characters long: {data}")

    # Handle any OS-related errors during the connection
    except OSError as e:
        print(f"OSError: {e}")


    # Close the client and server sockets when done
    print("Disconnected")
    client_sock.close()
    server_sock.close()
    print("All done")
