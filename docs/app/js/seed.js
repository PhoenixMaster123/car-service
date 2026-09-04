/*
 * Seed data for the static demo.
 *
 * The service catalogue is a copy of DataInitializer.seedData() in the real
 * application, so prices, durations and categories match what a running
 * instance creates on first boot. Everything else (users, vehicles, bookings,
 * invoices, news) stands in for rows a real database would hold.
 */

const CATEGORIES = [
  { id: 1, name: 'WASH_AND_DETAIL', displayName: 'Car Wash & Detailing' },
  { id: 2, name: 'MAINTENANCE_AND_REPAIR', displayName: 'Maintenance & Repair' },
  { id: 3, name: 'DIAGNOSTICS_AND_INSPECTION', displayName: 'Diagnostics & Inspection' },
];

const SERVICES = [
  { id: 1, name: 'Express Wash', description: 'Wash & Detailing', basePrice: 19.99, duration: 30, categoryId: 1 },
  { id: 2, name: 'Standard Wash', description: 'Wash & Detailing', basePrice: 29.99, duration: 45, categoryId: 1 },
  { id: 3, name: 'Interior Cleaning', description: 'Wash & Detailing', basePrice: 49.99, duration: 60, categoryId: 1 },
  { id: 4, name: 'Full-Service Wash', description: 'Wash & Detailing', basePrice: 59.99, duration: 90, categoryId: 1 },

  { id: 5, name: 'Oil and Filter Change', description: 'Oil & Filter Change', basePrice: 89.99, duration: 60, categoryId: 2 },
  { id: 6, name: 'Tire Rotation', description: 'Tire Rotation', basePrice: 24.99, duration: 30, categoryId: 2 },
  { id: 7, name: 'Tire Swap', description: 'Tire Swap', basePrice: 79.99, duration: 45, categoryId: 2 },
  { id: 8, name: 'Tire Puncture Repair', description: 'Tire Puncture Repair', basePrice: 34.99, duration: 30, categoryId: 2 },
  { id: 9, name: 'Brake Pad & Disc Replacement', description: 'Brake Pad & Disc Replacement', basePrice: 199.99, duration: 120, categoryId: 2 },
  { id: 10, name: 'Brake Fluid Change', description: 'Brake Fluid Change', basePrice: 69.99, duration: 45, categoryId: 2 },
  { id: 11, name: 'Fluid Checks & Top-Ups', description: 'Fluid Checks & Top-Ups', basePrice: 14.99, duration: 15, categoryId: 2 },
  { id: 12, name: 'Battery Replacement', description: 'Battery Replacement', basePrice: 129.99, duration: 30, categoryId: 2 },
  { id: 13, name: 'Wiper Blade Replacement', description: 'Wiper Blade Replacement', basePrice: 29.99, duration: 10, categoryId: 2 },

  { id: 14, name: 'Vehicle Health Check', description: 'Vehicle Health Check', basePrice: 49.99, duration: 45, categoryId: 3 },
  { id: 15, name: 'On-Board Diagnostics (OBD) Scan', description: 'OBD Scan', basePrice: 39.99, duration: 20, categoryId: 3 },
  { id: 16, name: 'Seasonal Inspection', description: 'Seasonal Inspection', basePrice: 59.99, duration: 60, categoryId: 3 },
  { id: 17, name: 'TUV Pre-Inspection', description: 'TUV Pre-Inspection', basePrice: 79.99, duration: 60, categoryId: 3 },
];

/* Dates are generated relative to today so the demo never looks stale. */
function dayOffset(days, hour = 9, minute = 0) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  d.setHours(hour, minute, 0, 0);
  return d.toISOString();
}

const USERS = [
  {
    id: 'u-1', firstName: 'Alex', lastName: 'Johnson', email: 'alex@demo.dev',
    password: 'demo1234', phoneNumber: '+49 151 2345678', country: 'Germany',
    role: 'USER', createdOn: dayOffset(-350, 10, 15), updatedOn: dayOffset(-12, 16, 40),
    twoFactorEnabled: false, loyaltyPoints: 340,
  },
  {
    id: 'u-2', firstName: 'Haris', lastName: 'Petrov', email: 'admin@demo.dev',
    password: 'admin1234', phoneNumber: '+359 88 123 4567', country: 'Bulgaria',
    role: 'ADMIN', createdOn: dayOffset(-720, 9, 0), updatedOn: dayOffset(-3, 11, 20),
    twoFactorEnabled: true, loyaltyPoints: 0,
  },
  {
    id: 'u-3', firstName: 'Sarah', lastName: 'Klein', email: 'sarah.klein@example.com',
    password: 'demo1234', phoneNumber: '+49 170 9988776', country: 'Germany',
    role: 'USER', createdOn: dayOffset(-210, 14, 30), updatedOn: dayOffset(-30, 9, 5),
    twoFactorEnabled: false, loyaltyPoints: 120,
  },
  {
    id: 'u-4', firstName: 'Mihail', lastName: 'Dimitrov', email: 'm.dimitrov@example.com',
    password: 'demo1234', phoneNumber: '+359 89 445 1122', country: 'Bulgaria',
    role: 'USER', createdOn: dayOffset(-160, 8, 45), updatedOn: dayOffset(-45, 13, 15),
    twoFactorEnabled: false, loyaltyPoints: 75,
  },
  {
    id: 'u-5', firstName: 'Elena', lastName: 'Nikolova', email: 'elena.n@example.com',
    password: 'demo1234', phoneNumber: '+359 87 220 3344', country: 'Bulgaria',
    role: 'USER', createdOn: dayOffset(-95, 17, 10), updatedOn: dayOffset(-8, 10, 0),
    twoFactorEnabled: false, loyaltyPoints: 210,
  },
  {
    id: 'u-6', firstName: 'Tomas', lastName: 'Novak', email: 't.novak@example.com',
    password: 'demo1234', phoneNumber: '+420 604 112 998', country: 'Czechia',
    role: 'USER', createdOn: dayOffset(-60, 12, 0), updatedOn: dayOffset(-20, 15, 45),
    twoFactorEnabled: false, loyaltyPoints: 40,
  },
  {
    id: 'u-7', firstName: 'Ivana', lastName: 'Georgieva', email: 'ivana.g@example.com',
    password: 'demo1234', phoneNumber: '+359 88 776 5544', country: 'Bulgaria',
    role: 'USER', createdOn: dayOffset(-28, 11, 25), updatedOn: dayOffset(-2, 18, 30),
    twoFactorEnabled: false, loyaltyPoints: 15,
  },
];

const VEHICLES = [
  {
    id: 'v-1', ownerId: 'u-1', make: 'Audi', model: 'A4', manufacturingYear: 2021,
    licensePlate: 'WUE-A123', vin: 'WAUZZZ8K9BA123456', color: 'Mythos Black',
    engineType: '2.0L TFSI', bodyType: 'Sedan',
  },
  {
    id: 'v-2', ownerId: 'u-1', make: 'Volkswagen', model: 'Golf', manufacturingYear: 2018,
    licensePlate: 'RSE-4477', vin: 'WVWZZZAUZJP009182', color: 'Pure White',
    engineType: '1.6 TDI', bodyType: 'Hatchback',
  },
];

const BOOKINGS = [
  {
    id: 'b-1', userId: 'u-1', vehicleId: 'v-1', serviceIds: [5, 11],
    bookingDate: dayOffset(6, 9, 30), status: 'PENDING', paymentMethod: 'CARD',
    phoneNumber: '+49 151 2345678', notes: '',
  },
  {
    id: 'b-2', userId: 'u-1', vehicleId: 'v-2', serviceIds: [2],
    bookingDate: dayOffset(13, 14, 0), status: 'PENDING', paymentMethod: 'CASH',
    phoneNumber: '+49 151 2345678', notes: 'Please check the rear wiper.',
  },
  {
    id: 'b-3', userId: 'u-1', vehicleId: 'v-1', serviceIds: [4],
    bookingDate: dayOffset(-24, 11, 0), status: 'PAID', paymentMethod: 'CARD',
    phoneNumber: '+49 151 2345678', notes: '',
  },
  {
    id: 'b-4', userId: 'u-1', vehicleId: 'v-1', serviceIds: [16, 15],
    bookingDate: dayOffset(-68, 10, 15), status: 'PAID', paymentMethod: 'PAYPAL',
    phoneNumber: '+49 151 2345678', notes: '',
  },
  {
    id: 'b-5', userId: 'u-1', vehicleId: 'v-2', serviceIds: [7],
    bookingDate: dayOffset(-140, 8, 45), status: 'COMPLETED', paymentMethod: 'CASH',
    phoneNumber: '+49 151 2345678', notes: '',
  },
  /* Other customers' bookings — these only surface in the admin table. */
  {
    id: 'b-6', userId: 'u-3', vehicleId: null, vehicleLabel: 'BMW 320d (M-KL 8892)',
    serviceIds: [9], bookingDate: dayOffset(2, 8, 0), status: 'PENDING',
    paymentMethod: 'CARD', phoneNumber: '+49 170 9988776', notes: '',
  },
  {
    id: 'b-7', userId: 'u-4', vehicleId: null, vehicleLabel: 'Opel Astra (P-4471-KH)',
    serviceIds: [1], bookingDate: dayOffset(1, 16, 30), status: 'PENDING',
    paymentMethod: 'CASH', phoneNumber: '+359 89 445 1122', notes: '',
  },
  {
    id: 'b-8', userId: 'u-5', vehicleId: null, vehicleLabel: 'Toyota Corolla (CB-1290-AC)',
    serviceIds: [12, 13], bookingDate: dayOffset(3, 13, 15), status: 'PENDING',
    paymentMethod: 'CARD', phoneNumber: '+359 87 220 3344', notes: '',
  },
  {
    id: 'b-9', userId: 'u-6', vehicleId: null, vehicleLabel: 'Skoda Octavia (4AB 9910)',
    serviceIds: [17], bookingDate: dayOffset(-5, 9, 0), status: 'CANCELLED',
    paymentMethod: 'CARD', phoneNumber: '+420 604 112 998', notes: '',
  },
];

const INVOICES = [
  {
    id: 'i-1', bookingId: 'b-3', userId: 'u-1', invoiceNumber: 'INV-2026-000118',
    status: 'PAID', paymentMethod: 'CARD', serviceDate: dayOffset(-24, 11, 0),
    issuedAt: dayOffset(-24, 12, 30), dueDate: dayOffset(-10, 0, 0),
    vehicleId: 'v-1', lineItemIds: [4], taxRate: 0.20,
  },
  {
    id: 'i-2', bookingId: 'b-4', userId: 'u-1', invoiceNumber: 'INV-2026-000074',
    status: 'PAID', paymentMethod: 'PAYPAL', serviceDate: dayOffset(-68, 10, 15),
    issuedAt: dayOffset(-68, 11, 45), dueDate: dayOffset(-54, 0, 0),
    vehicleId: 'v-1', lineItemIds: [16, 15], taxRate: 0.20,
  },
  {
    id: 'i-3', bookingId: 'b-5', userId: 'u-1', invoiceNumber: 'INV-2025-000903',
    status: 'OVERDUE', paymentMethod: 'CASH', serviceDate: dayOffset(-140, 8, 45),
    issuedAt: dayOffset(-140, 10, 0), dueDate: dayOffset(-126, 0, 0),
    vehicleId: 'v-2', lineItemIds: [7], taxRate: 0.20,
  },
];

const NEWS = [
  {
    id: 'n-1', title: 'New ceramic coating line is live',
    author: 'Haris P.', dateCreated: dayOffset(-4, 9, 0),
    content: 'We have added a two-stage ceramic coating to the detailing menu. It bonds to the clear coat '
      + 'for up to three years of gloss and hydrophobic protection, and it is now bookable online.',
  },
  {
    id: 'n-2', title: 'Deep-extraction carpet machine in the wash bay',
    author: 'Sarah J.', dateCreated: dayOffset(-19, 14, 30),
    content: 'Our new hot-water extraction unit lifts embedded dirt that a normal vacuum leaves behind. '
      + 'Interior Cleaning bookings now include a pass with it at no extra cost.',
  },
  {
    id: 'n-3', title: 'Winter tyre swap slots are open',
    author: 'Mike T.', dateCreated: dayOffset(-33, 8, 15),
    content: 'Seasonal changeover slots fill up fast. Book a Tire Swap now and we will store your summer '
      + 'set for free over the winter months.',
  },
];

window.DEMO_SEED = {
  categories: CATEGORIES,
  services: SERVICES,
  users: USERS,
  vehicles: VEHICLES,
  bookings: BOOKINGS,
  invoices: INVOICES,
  news: NEWS,
};
