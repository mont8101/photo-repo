## Welcome to File Cabinet. 

File Cabinet is a full-stack photo storage and management solution designed with enterprise-grade architecture principles. 
This Spring Boot backend provides comprehensive RESTful APIs for photo upload, storage, retrieval, and management operations, with support for infinite scroll pagination and user interaction features.

## Technology Stack

Framework: Spring Boot 3.x  
Database: MongoDB with GridFS for file storage  
Data Access: Spring Data MongoDB  
File Upload: Spring Web MultipartFile handling  
Build Tool: Gradle  
Java Version: 17+  

## Key Features

Photo Upload & Storage: Secure file upload with GridFS integration for efficient large file handling  
Like System: Per-user like tracking  
Infinite Scroll: Paginated endpoints with sorting by upload date or popularity  
File Operations: Download, view, and thumbnail endpoints for flexible file access  
Validation: Comprehensive file validation (type, size, format)  
Metadata Management: Rich artifact metadata with upload tracking  

## Architecture & Design Principles
### Clean Architecture Implementation

## Project Structure
```
web/                      # REST Controllers  
service/                  # Business Logic Interfaces  
  impl/                   # Service Implementations  
model/                    # Domain Entities  
repository/               # Data Access Layer  
```
## Object-Oriented Design Patterns

Interface Separation: Service contracts defined through interfaces  
Dependency Injection: Spring's @Autowired for loose coupling and testability  
Repository Pattern: Spring Data MongoDB repositories for data persistence  
Service Layer Pattern: Business logic encapsulation in service implementations  
Single Responsibility: Each class focused on specific domain concerns  

## Spring Framework Integration

REST Controllers: @RestController with comprehensive endpoint mapping  
Component Management: Automatic dependency injection and component scanning  
Data Access: Spring Data MongoDB with custom query methods  
File Storage: GridFS integration for large file management  
Exception Handling: Centralized error handling with ResponseStatusException  

## API Endpoints

### Artifact Management
```
GET    /api/artifacts/              # Get paginated artifacts with sorting  
GET    /api/artifacts/{id}          # Get specific artifact metadata  
POST   /api/artifacts/              # Upload new file   
DELETE /api/artifacts/{id}          # Delete artifact and associated file  
```
### File Operations
```
GET    /api/download/{id}           # Download original file  
GET    /api/view/{id}               # View file in browser  
GET    /api/thumbnail/{id}          # Get thumbnail  
```
### Search & Filtering
```
GET    /api/artifacts/search?fileName={name}     # Search by filename  
GET    /api/artifacts/by-type?contentType={type} # Filter by content type  
```
### Like System
```
POST   /api/artifacts/{id}/like           # Increment like count  
DELETE /api/artifacts/{id}/like           # Decrement like count  
```
## Data Models

### Artifact Entity
- id: String (UUID)  
- fileName: String (user-defined name)  
- contentType: String (MIME type)  
- fileSize: long (bytes)  
- uploadDate: LocalDateTime  
- gridFsId: ObjectId (GridFS reference)  
- likeCount: int  
- likedByUsers: Set<String> (user tracking)  

### Profile Entity
- id: String (UUID)  
- displayName: String  
- email: String  
- avatarId: String  

## Front-end Integration
This backend is designed to work with a React frontend that supports:  

Infinite scroll photo galleries  
File upload with drag-and-drop  
Real-time like interactions  
Responsive image display  

The front-end repository is here:  
https://github.com/mont8101/photo-repo-front-end  

## How to run locally

Local Mongo instance set up in Docker:  
Pull the MongoDB Docker Image  
```
  docker pull mongodb/mongodb-community-server:latest
```
Run the Image as a Container  
```
  docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest  
```
set application-local.properties   
```
  spring.data.mongodb.uri=mongodb://localhost:27017/mongodb  
  spring.data.mongodb.database=filecabinet  
  server.port=8080  
```
Edit FileCabinetApplication run configuration  
  Add VM options  
  -Dspring.profiles.active=local  

Run FileCabinetApplication  
