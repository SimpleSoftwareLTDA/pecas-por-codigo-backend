# Use Bun image for consistency
FROM oven/bun:alpine

WORKDIR /app

# Copy the static files and package.json
COPY index.html style.css app.js package.json ./

# Expose port 3000 (default for serve)
EXPOSE 3000

# Serve the static files using -l for listen port
CMD ["bun", "x", "serve", ".", "-l", "3000"]
