// MongoDB initialization script
// Runs once when the container is first created

db = db.getSiblingDB('webgara');

// Create collections with validation
db.createCollection('users');
db.createCollection('garages');
db.createCollection('vehicles');
db.createCollection('services');

// Create indexes
db.users.createIndex({ email: 1 }, { unique: true });
db.garages.createIndex({ slug: 1 }, { unique: true });
db.garages.createIndex({ 'address.city': 1 });
db.vehicles.createIndex({ plateNumber: 1 }, { unique: true });
db.vehicles.createIndex({ ownerId: 1 });
db.services.createIndex({ garageId: 1 });
db.services.createIndex({ category: 1 });

print('MongoDB initialization completed for webgara database');
