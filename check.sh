#!/bin/bash
echo "Running Checkstyle..."
mvn checkstyle:checkstyle

if [ -f "target/site/checkstyle.html" ]; then
    echo "Report generated. Opening in your browser..."

    # Convert the Linux path to a Windows path
    WIN_PATH=$(wslpath -w "$(pwd)/target/site/checkstyle.html")

    # Use explorer.exe to open the file in Windows
    explorer.exe "$WIN_PATH"
else
    echo "Error: Checkstyle report was not generated."
fi