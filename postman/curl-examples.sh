#!/bin/sh
# Create a parking slot
curl -i -X POST http://localhost:8080/api/slots \
  -H 'Content-Type: application/json' \
  -d '{"slotNumber":"A-01"}'

# List available slots
curl -i http://localhost:8080/api/slots/available

# Book a slot (replace slotId with an ID returned by the API)
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"vehicleNumber":"MH31AB1234","slotId":1}'

# View active bookings
curl -i http://localhost:8080/api/bookings/active

# Calculate fare at a chosen timestamp (replace booking ID/time as needed)
curl -i 'http://localhost:8080/api/bookings/1/fare?at=2026-10-25T13:05:00'

# Check out vehicle
curl -i -X POST http://localhost:8080/api/bookings/1/checkout

# Revenue for a date
curl -i 'http://localhost:8080/api/reports/revenue?date=2026-10-25'
