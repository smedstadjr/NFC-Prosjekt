import unittest
from unittest.mock import patch, MagicMock


# Define a test case class for GPIO control
class TestGPIOControl(unittest.TestCase):

    # Mocking GPIO module for the lock test
    @patch('src.gpio_control.GPIO', new_callable=MagicMock)
    def test_lock(self, MockGPIO):
        from src.gpio_control import lock  # Importing the lock function
        lock()  # Call the lock function
        # Checking if GPIO pins were set to HIGH and LOW
        MockGPIO.output.assert_any_call(23, MockGPIO.HIGH)
        MockGPIO.output.assert_any_call(24, MockGPIO.LOW)


    # Mocking the GPIO module for the unlock test
    @patch('src.gpio_control.GPIO', new_callable=MagicMock)
    def test_unlock(self, MockGPIO):
        from src.gpio_control import unlock  # Import the unlock function
        unlock()  # Calling the unlock function
        # Checking if GPIO pins were set to LOW and HIGH
        MockGPIO.output.assert_any_call(23, MockGPIO.LOW)
        MockGPIO.output.assert_any_call(24, MockGPIO.HIGH)


# Run the tests if this file is executed
if __name__ == '__main__':
    unittest.main()
