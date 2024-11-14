import RPi.GPIO as GPIO  # Library for controlling Raspberry Pi GPIO pins
import time  # Module to handle time relevant items


# Turn off GPIO warnings and setting the mode to use board pin numbering
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD) # We use board pin numbering for connected components


# GPIO pin numbers for the servo motor and the LEDs
LOCK_PIN = 12  # Pin controlling the lock (servo motor)
LED_LOCKED_PIN = 23  # Pin for the "locked" LED, red
LED_UNLOCKED_PIN = 24  # Pin for the "unlocked" LED, green


# Set up each pin as an output to control the servo and LEDs
GPIO.setup(LOCK_PIN, GPIO.OUT)
GPIO.setup(LED_LOCKED_PIN, GPIO.OUT)
GPIO.setup(LED_UNLOCKED_PIN, GPIO.OUT)


# Initialize PWM on the lock pin to control the servo motor
servo1 = GPIO.PWM(LOCK_PIN, 50)  # 50Hz frequency to reduce stutter
servo1.start(0)  # Start PWM with 0 duty cycle


# Start with the lock in the "locked" state
is_locked = True


# Function to lock the device
def lock():
    global is_locked  # Use the global is_locked variable
    GPIO.output(LED_LOCKED_PIN, GPIO.HIGH)  # Turn on "locked" LED
    GPIO.output(LED_UNLOCKED_PIN, GPIO.LOW)  # Turn off "unlocked" LED
    servo1.ChangeDutyCycle(12.5)  # Move servo to locked position
    time.sleep(1)  # Wait for servo to move to the position
    servo1.ChangeDutyCycle(0)  # Stop PWM signal to hold position
    is_locked = True  # Update state to "locked"
    print("Locked")  # Output status


# Function to unlock the device
def unlock():
    global is_locked  # Use the global is_locked variable
    GPIO.output(LED_LOCKED_PIN, GPIO.LOW)  # Turn off "locked" LED
    GPIO.output(LED_UNLOCKED_PIN, GPIO.HIGH)  # Turn on "unlocked" LED
    servo1.ChangeDutyCycle(2.5)  # Move servo to unlocked position
    time.sleep(1)  # Wait for servo to move to the position
    servo1.ChangeDutyCycle(0)  # Stop PWM signal to hold position
    is_locked = False  # Update state to "unlocked"
    print("Unlocked")  # Output status


# Function to clean up GPIO settings when finished
def cleanup():
    servo1.stop()  # Stop PWM for the servo motor
    GPIO.cleanup()  # Clean GPIO pin status
