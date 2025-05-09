#!/bin/bash

# Configuration
BASE_URL="http://192.168.1.175:8081"
TOKEN=""
TEST_USER_ID=""
TEST_GEOEVENT_ID=""
TEST_GEOSTAMP_ID=""
TEST_MESSAGE_ID=""

# Colors for output
GREEN="\e[32m"
RED="\e[31m"
YELLOW="\e[33m"
RESET="\e[0m"

# Function to run test and display result
run_test() {
    local method="$1"
    local endpoint="$2"
    local data="${3:-}"
    local expected_status="${4:-200}"
    
    echo -e "${YELLOW}Testing $method $endpoint${RESET}"
    
    # Prepare curl command
    local curl_cmd="curl -s -X $method '$BASE_URL$endpoint'"
    
    # Add authorization header if token exists
    if [ -n "$TOKEN" ]; then
        curl_cmd+=" -H 'Authorization: Bearer $TOKEN'"
    fi
    
    # Add data for POST/PUT requests
    if [ -n "$data" ]; then
        curl_cmd+=" -H 'Content-Type: application/json' -d '$data'"
    fi
    
    # Capture response and status code
    local response=$(eval "$curl_cmd")
    local status_code=$(eval "$curl_cmd -o /dev/null -w '%{http_code}'" 2>/dev/null)
    
    # Check status code
    if [ "$status_code" -eq "$expected_status" ]; then
        echo -e "${GREEN}✓ $endpoint - SUCCESS (Status $status_code)${RESET}"
        return 0
    else
        echo -e "${RED}✗ $endpoint - FAILED (Expected $expected_status, Got $status_code)${RESET}"
        echo "Response: $response"
        return 1
    fi
}

# Main test function
run_openapi_tests() {
    # User Registration
    run_test "POST" "/users" '{"id":"test_user","phoneNumber":"+1234567890","name":"Test User"}' 201
    TEST_USER_ID="test_user"
    
    # Authentication
    run_test "POST" "/auth" '{"phoneNumber":"+1234567890","password":"testpassword"}' 200
    # Capture the token (you'll need to modify this based on actual auth response)
    TOKEN="sample_jwt_token"
    
    # Get User Details
    run_test "GET" "/users/$TEST_USER_ID"
    
    # Update User
    run_test "PUT" "/users/$TEST_USER_ID" '{"phoneNumber":"+1234567891","name":"Updated Test User"}'
    
    # Create GeoEvent
    run_test "POST" "/geoevents" '{"id":"test_geoevent","userId":"test_user"}' 201
    TEST_GEOEVENT_ID="test_geoevent"
    
    # List GeoEvents
    run_test "GET" "/geoevents"
    
    # Get Specific GeoEvent
    run_test "GET" "/geoevents/$TEST_GEOEVENT_ID"
    
    # Create GeoStamp
    run_test "POST" "/geostamps" '{"id":"test_geostamp","userId":"test_user","geoEventId":"test_geoevent"}' 201
    TEST_GEOSTAMP_ID="test_geostamp"
    
    # List GeoStamps
    run_test "GET" "/geostamps"
    
    # Get Specific GeoStamp
    run_test "GET" "/geostamps/$TEST_GEOSTAMP_ID"
    
    # Update GeoStamp
    run_test "PUT" "/geostamps/$TEST_GEOSTAMP_ID" '{"id":"test_geostamp","geoEventId":"test_geoevent_updated"}'
    
    # Create Chat Message
    run_test "POST" "/msg" '{"id":"test_message","eventId":"test_geoevent","content":"Test message"}' 201
    TEST_MESSAGE_ID="test_message"
    
    # List Chat Messages
    run_test "GET" "/msg?eventId=test_geoevent"
    
    # Update Chat Message
    run_test "PUT" "/msg" '{"id":"test_message","eventId":"test_geoevent","content":"Updated test message"}'
    
    # Cleanup tests (Delete operations)
    run_test "DELETE" "/msg/$TEST_MESSAGE_ID"
    run_test "DELETE" "/geostamps/$TEST_GEOSTAMP_ID"
    run_test "DELETE" "/geoevents/$TEST_GEOEVENT_ID"
    run_test "DELETE" "/users/$TEST_USER_ID"
}

# Run tests
echo "Starting OpenAPI Tests..."
run_openapi_tests

# Exit with error if any tests failed
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Some tests FAILED${RESET}"
    exit 1
else
    echo -e "${GREEN}✅ All tests PASSED${RESET}"
fi