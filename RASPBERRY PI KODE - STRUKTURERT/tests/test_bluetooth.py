import unittest  # Import the unittest module for creating and running tests
from unittest.mock import patch, MagicMock  # Import patch and MagicMock for mocking


# Define a test case class for the Bluetooth server
class TestBluetoothServer(unittest.TestCase):

    # Mocking the BluetoothSocket for the test
    @patch('bluetooth.BluetoothSocket')
    def test_bluetooth_server(self, MockBluetoothSocket):
        from src.bluetooth_server import bluetooth_server  # Import the bluetooth_server function

        # Mock server and client sockets
        mock_server_socket = MagicMock()
        mock_client_socket = MagicMock()

        # Configure MockBluetoothSocket to return the mock server socket
        MockBluetoothSocket.return_value = mock_server_socket
        # Configure the server socket to return the mocking client socket and client info
        mock_server_socket.accept.return_value = (mock_client_socket, ('00:00:00:00:00:00', 1))

        # Set up recv to return data
        mock_client_socket.recv.side_effect = [b'000100650003', b'']  # Simulate data and then disconnect

        # Calling the bluetooth_server function with mock lock and unlock functions
        bluetooth_server(lambda: print("Lock"), lambda: print("Unlock"))

        # Ensure recv was called at least once on the client socket
        self.assertGreaterEqual(mock_client_socket.recv.call_count, 1)

        # Verify the client socket was closed
        mock_client_socket.close.assert_called()

        # Verify the server socket was closed
        mock_server_socket.close.assert_called()


# Run the tests if this file is executed
if __name__ == '__main__':
    unittest.main()
