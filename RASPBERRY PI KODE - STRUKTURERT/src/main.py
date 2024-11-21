import threading  # Library for working with threads, used for parallell usage in bluetooth server
from src.gpio_control import lock, unlock, cleanup  # Import lock, unlock, and cleanup functions from gpio_control module
from src.bluetooth_server import bluetooth_server  # Import the Bluetooth server function for handling Bluetooth communication
from smartcard.System import readers  # Library for interacting with NFC card readers
from smartcard.util import toHexString  # Used for converting card UID to a hex string format
from smartcard.Exceptions import NoCardException, CardConnectionException  # Handling exceptions when no card is detected or connection issues
import time  # Library for managing delays


def main():
    # Starting Bluetooth server in a separate thread to handle Bluetooth communication
    bt_thread = threading.Thread(target=bluetooth_server, args=(lock, unlock))
    bt_thread.daemon = True  # Set thread as daemon so it will exit when the RASPBERRY PI KODE - STRUKTURERT program exits (pi OS)
    bt_thread.start()

    # NFC reader
    r = readers()  # Get the list of available NFC readers
    if len(r) == 0:  # Check if no NFC readers are found
        print("No NFC readers found")
        exit()  # Exit the program if no readers are found
    reader = r[0]  # Use the first available reader
    print("Waiting for NFC card...")  # Print message to indicate the program is waiting for NFC card

    while True:  # To run continuously
        try:
            connection = reader.createConnection()  # Create connection with NFC reader
            connection.connect()  # Establish connection with the NFC reader
            data, sw1, sw2 = connection.transmit([0xFF, 0xCA, 0x00, 0x00, 0x00])  # Send command to read UID of the card
            uid = toHexString(data)  # Convert the card UID to a hex string
            if uid:  # If UID is found (card detected)
                print(f"Found card with UID: {uid}")
                if uid == "85 48 16 2D":  # Check if the UID matches a predefined one
                    if is_locked:  # If the lock is currently locked, unlock it, otherwise lock it
                        unlock()
                    else:
                        lock()
                time.sleep(0.5)  # Delay
        except NoCardException:  # Handle case where no card is present in the reader
            pass  # Continue to wait for a card and do nothing
        except CardConnectionException:  # Handle connection issues with the card reader
            pass  # Do nothing and continue
        time.sleep(0.1)  # Short delay


# Entry point of the program
if __name__ == "__main__":
    try:
        main()  # Call the RASPBERRY PI KODE - STRUKTURERT function to start the program
    except KeyboardInterrupt:  # Handle the keyboard interrupt
        print("Exiting...")  # Print message on program exit
    finally:
        cleanup()  # Clean up when done
