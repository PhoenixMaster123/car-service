/*
 * Demo state.
 *
 * Stands in for the JPA repositories: everything the real application would
 * read from MySQL lives here in sessionStorage instead. State survives
 * navigation between pages and is thrown away when the tab closes, so every
 * visitor gets the same starting point.
 */

const STORAGE_KEY = 'car-service-demo:v1';

function clone(value) {
  return JSON.parse(JSON.stringify(value));
}

function freshState() {
  const seed = window.DEMO_SEED;
  return {
    users: clone(seed.users),
    vehicles: clone(seed.vehicles),
    bookings: clone(seed.bookings),
    invoices: clone(seed.invoices),
    services: clone(seed.services),
    categories: clone(seed.categories),
    news: clone(seed.news),
    cart: [],
    session: null,
    pendingTwoFactor: null,
    flash: null,
    nextInvoiceNumber: 119,
    notifications: { emailNewBooking: true, emailBookingCancel: true, emailReminder: true },
  };
}

let state = null;

function load() {
  if (state) return state;
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (raw) {
      state = JSON.parse(raw);
      /* A seed change between visits must not resurrect a half-shaped object. */
      if (state && state.users && state.services) return state;
    }
  } catch (err) {
    /* Private mode, blocked storage — fall through to a fresh in-memory state. */
  }
  state = freshState();
  save();
  return state;
}

function save() {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  } catch (err) {
    /* Storage unavailable; the page still works for the current view. */
  }
}

function nextId(prefix) {
  return prefix + '-' + Math.random().toString(36).slice(2, 9);
}

const Store = {
  reset() {
    state = freshState();
    save();
  },

  /* ---------------------------------------------------------------- session */

  currentUser() {
    const s = load();
    if (!s.session) return null;
    return s.users.find((u) => u.id === s.session) || null;
  },

  isAuthenticated() {
    return Store.currentUser() !== null;
  },

  isAdmin() {
    const user = Store.currentUser();
    return user !== null && user.role === 'ADMIN';
  },

  /**
   * Mirrors the real sign-in: wrong credentials are rejected, and an account
   * with 2FA switched on gets sent to the code screen instead of straight in.
   */
  signIn(email, password) {
    const s = load();
    const user = s.users.find(
      (u) => u.email.toLowerCase() === String(email || '').trim().toLowerCase(),
    );
    if (!user || user.password !== password) {
      return { ok: false, error: 'Invalid email or password.' };
    }
    if (user.twoFactorEnabled) {
      s.pendingTwoFactor = user.id;
      save();
      return { ok: true, twoFactor: true };
    }
    s.session = user.id;
    s.pendingTwoFactor = null;
    save();
    return { ok: true, twoFactor: false, user };
  },

  /** The demo accepts any six digits — there is no mail server behind it. */
  verifyTwoFactor(code) {
    const s = load();
    if (!s.pendingTwoFactor) return { ok: false, error: 'Your sign-in attempt expired. Start again.' };
    if (!/^\d{6}$/.test(String(code || '').trim())) {
      return { ok: false, error: 'Enter the six-digit code from your email.' };
    }
    s.session = s.pendingTwoFactor;
    s.pendingTwoFactor = null;
    save();
    return { ok: true };
  },

  pendingTwoFactorUser() {
    const s = load();
    if (!s.pendingTwoFactor) return null;
    return s.users.find((u) => u.id === s.pendingTwoFactor) || null;
  },

  register(data) {
    const s = load();
    const email = String(data.email || '').trim().toLowerCase();
    if (s.users.some((u) => u.email.toLowerCase() === email)) {
      return { ok: false, error: 'An account with that email already exists.' };
    }
    if (data.password !== data.confirmPassword) {
      return { ok: false, error: 'The two passwords do not match.' };
    }
    const user = {
      id: nextId('u'),
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      password: data.password,
      phoneNumber: '',
      country: 'Germany',
      role: 'USER',
      createdOn: new Date().toISOString(),
      updatedOn: new Date().toISOString(),
      twoFactorEnabled: false,
      loyaltyPoints: 0,
    };
    s.users.push(user);
    s.session = user.id;
    save();
    return { ok: true, user };
  },

  signOut() {
    const s = load();
    s.session = null;
    s.pendingTwoFactor = null;
    s.cart = [];
    save();
  },

  /* ------------------------------------------------------------------ flash */

  setFlash(type, text) {
    const s = load();
    s.flash = { type, text };
    save();
  },

  takeFlash() {
    const s = load();
    const flash = s.flash;
    s.flash = null;
    save();
    return flash;
  },

  /* --------------------------------------------------------------- catalogue */

  categories() {
    return load().categories;
  },

  services() {
    return load().services;
  },

  service(id) {
    return load().services.find((sv) => String(sv.id) === String(id)) || null;
  },

  servicesByCategory() {
    const s = load();
    return s.categories.map((cat) => ({
      category: cat,
      services: s.services.filter((sv) => sv.categoryId === cat.id),
    }));
  },

  addService(data) {
    const s = load();
    const service = {
      id: Math.max(0, ...s.services.map((sv) => Number(sv.id))) + 1,
      name: data.name,
      description: data.description,
      basePrice: Number(data.basePrice),
      duration: Number(data.duration),
      categoryId: Number(data.categoryId),
    };
    s.services.push(service);
    save();
    return service;
  },

  updateService(id, data) {
    const s = load();
    const service = s.services.find((sv) => String(sv.id) === String(id));
    if (!service) return null;
    service.name = data.name;
    service.description = data.description;
    service.basePrice = Number(data.basePrice);
    service.duration = Number(data.duration);
    service.categoryId = Number(data.categoryId);
    save();
    return service;
  },

  deleteService(id) {
    const s = load();
    s.services = s.services.filter((sv) => String(sv.id) !== String(id));
    save();
  },

  /* ----------------------------------------------------------------- vehicles */

  vehicles(userId) {
    const s = load();
    const owner = userId || s.session;
    return s.vehicles.filter((v) => v.ownerId === owner);
  },

  vehicle(id) {
    return load().vehicles.find((v) => v.id === id) || null;
  },

  addVehicle(data) {
    const s = load();
    const plate = String(data.licensePlate || '').trim().toUpperCase();
    if (s.vehicles.some((v) => v.ownerId === s.session && v.licensePlate.toUpperCase() === plate)) {
      return { ok: false, error: 'A vehicle with that license plate is already in your garage.' };
    }
    const vehicle = {
      id: nextId('v'),
      ownerId: s.session,
      make: data.make,
      model: data.model,
      manufacturingYear: Number(data.manufacturingYear),
      licensePlate: data.licensePlate,
      vin: data.vin,
      color: data.color || '—',
      engineType: data.engineType || 'N/A',
      bodyType: data.bodyType || 'N/A',
    };
    s.vehicles.push(vehicle);
    save();
    return { ok: true, vehicle };
  },

  updateVehicle(id, data) {
    const s = load();
    const vehicle = s.vehicles.find((v) => v.id === id);
    if (!vehicle) return { ok: false, error: 'Vehicle not found.' };
    vehicle.make = data.make;
    vehicle.model = data.model;
    vehicle.manufacturingYear = Number(data.manufacturingYear);
    vehicle.licensePlate = data.licensePlate;
    vehicle.vin = data.vin;
    vehicle.color = data.color;
    save();
    return { ok: true, vehicle };
  },

  deleteVehicle(id) {
    const s = load();
    s.vehicles = s.vehicles.filter((v) => v.id !== id);
    save();
  },

  /* --------------------------------------------------------------------- cart */

  cart() {
    const s = load();
    const items = s.cart
      .map((id) => s.services.find((sv) => String(sv.id) === String(id)))
      .filter(Boolean);
    const total = items.reduce((sum, item) => sum + item.basePrice, 0);
    return { items, count: items.length, total };
  },

  addToCart(serviceId) {
    const s = load();
    s.cart.push(Number(serviceId));
    save();
  },

  removeFromCart(serviceId) {
    const s = load();
    const index = s.cart.findIndex((id) => String(id) === String(serviceId));
    if (index >= 0) s.cart.splice(index, 1);
    save();
  },

  clearCart() {
    const s = load();
    s.cart = [];
    save();
  },

  /* ----------------------------------------------------------------- bookings */

  /** Denormalises a booking the way the BookingResponse DTO does server-side. */
  describeBooking(booking) {
    const s = load();
    const services = booking.serviceIds
      .map((id) => s.services.find((sv) => String(sv.id) === String(id)))
      .filter(Boolean);
    let vehicleDescription = booking.vehicleLabel || 'N/A';
    if (booking.vehicleId) {
      const vehicle = s.vehicles.find((v) => v.id === booking.vehicleId);
      if (vehicle) {
        vehicleDescription = vehicle.make + ' ' + vehicle.model + ' (' + vehicle.licensePlate + ')';
      }
    }
    const user = s.users.find((u) => u.id === booking.userId);
    return Object.assign({}, booking, {
      serviceNames: services.map((sv) => sv.name).join(', ') || 'Service',
      services,
      vehicleDescription,
      totalPrice: services.reduce((sum, sv) => sum + sv.basePrice, 0),
      customerName: user ? user.firstName + ' ' + user.lastName : 'Unknown',
    });
  },

  bookings(userId) {
    const s = load();
    const owner = userId || s.session;
    return s.bookings
      .filter((b) => b.userId === owner)
      .map(Store.describeBooking)
      .sort((a, b) => new Date(a.bookingDate) - new Date(b.bookingDate));
  },

  allBookings() {
    const s = load();
    return s.bookings
      .filter((b) => !b.archived)
      .map(Store.describeBooking)
      .sort((a, b) => new Date(b.bookingDate) - new Date(a.bookingDate));
  },

  upcomingBookings() {
    const now = Date.now();
    return Store.bookings().filter(
      (b) => new Date(b.bookingDate).getTime() >= now && b.status !== 'CANCELLED',
    );
  },

  pastBookings() {
    const now = Date.now();
    return Store.bookings()
      .filter((b) => new Date(b.bookingDate).getTime() < now || b.status === 'CANCELLED')
      .reverse();
  },

  createBooking(data) {
    const s = load();
    const serviceIds = s.cart.slice();
    if (serviceIds.length === 0) {
      return { ok: false, error: 'Select at least one service before booking.' };
    }
    const booking = {
      id: nextId('b'),
      userId: s.session,
      vehicleId: data.vehicleId,
      serviceIds,
      bookingDate: new Date(data.bookingDate).toISOString(),
      status: 'PENDING',
      paymentMethod: data.paymentMethod,
      phoneNumber: data.phoneNumber,
      notes: data.notes || '',
    };
    s.bookings.push(booking);
    save();
    return { ok: true, booking: Store.describeBooking(booking) };
  },

  cancelBooking(id) {
    const s = load();
    const booking = s.bookings.find((b) => b.id === id);
    if (!booking) return;
    booking.status = 'CANCELLED';
    save();
  },

  archiveBooking(id) {
    const s = load();
    const booking = s.bookings.find((b) => b.id === id);
    if (!booking) return;
    booking.archived = true;
    save();
  },

  /* ----------------------------------------------------------------- invoices */

  /** Expands an invoice into the shape invoice-detail.html renders. */
  describeInvoice(invoice) {
    const s = load();
    const user = s.users.find((u) => u.id === invoice.userId);
    const vehicle = s.vehicles.find((v) => v.id === invoice.vehicleId);
    const lineItems = invoice.lineItemIds
      .map((id) => s.services.find((sv) => String(sv.id) === String(id)))
      .filter(Boolean);
    const subtotal = lineItems.reduce((sum, item) => sum + item.basePrice, 0);
    const taxAmount = subtotal * invoice.taxRate;
    return Object.assign({}, invoice, {
      customerName: user ? user.firstName + ' ' + user.lastName : 'Unknown',
      customerEmail: user ? user.email : '',
      customerPhone: user ? user.phoneNumber : '',
      vehicleDescription: vehicle
        ? vehicle.make + ' ' + vehicle.model + ' (' + vehicle.licensePlate + ')'
        : '—',
      lineItems,
      subtotal,
      taxAmount,
      total: subtotal + taxAmount,
    });
  },

  invoices(userId) {
    const s = load();
    const owner = userId || s.session;
    return s.invoices
      .filter((i) => i.userId === owner)
      .map(Store.describeInvoice)
      .sort((a, b) => new Date(b.issuedAt) - new Date(a.issuedAt));
  },

  invoice(id) {
    const s = load();
    const invoice = s.invoices.find((i) => i.id === id);
    return invoice ? Store.describeInvoice(invoice) : null;
  },

  /**
   * What the payment callback does server-side: mark the booking paid and
   * raise the invoice for it.
   */
  payForBooking(bookingId) {
    const s = load();
    const booking = s.bookings.find((b) => b.id === bookingId);
    if (!booking) return null;
    booking.status = 'PAID';
    const number = 'INV-2026-' + String(s.nextInvoiceNumber).padStart(6, '0');
    s.nextInvoiceNumber += 1;
    const invoice = {
      id: nextId('i'),
      bookingId: booking.id,
      userId: booking.userId,
      invoiceNumber: number,
      status: 'PAID',
      paymentMethod: booking.paymentMethod,
      serviceDate: booking.bookingDate,
      issuedAt: new Date().toISOString(),
      dueDate: null,
      vehicleId: booking.vehicleId,
      lineItemIds: booking.serviceIds.slice(),
      taxRate: 0.20,
    };
    s.invoices.push(invoice);
    s.cart = [];
    save();
    return Store.describeInvoice(invoice);
  },

  /* -------------------------------------------------------------------- users */

  allUsers() {
    return load().users;
  },

  user(id) {
    return load().users.find((u) => u.id === id) || null;
  },

  addUser(data) {
    const s = load();
    const user = {
      id: nextId('u'),
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      password: 'demo1234',
      phoneNumber: data.phoneNumber || '',
      country: data.country || 'Germany',
      role: data.role || 'USER',
      createdOn: new Date().toISOString(),
      updatedOn: new Date().toISOString(),
      twoFactorEnabled: false,
      loyaltyPoints: 0,
    };
    s.users.push(user);
    save();
    return user;
  },

  updateUser(id, data) {
    const s = load();
    const user = s.users.find((u) => u.id === id);
    if (!user) return null;
    Object.assign(user, data, { updatedOn: new Date().toISOString() });
    save();
    return user;
  },

  deleteUser(id) {
    const s = load();
    s.users = s.users.filter((u) => u.id !== id);
    save();
  },

  changePassword(current, next) {
    const user = Store.currentUser();
    if (!user) return { ok: false, error: 'Not signed in.' };
    if (user.password !== current) return { ok: false, error: 'Your current password is not correct.' };
    if (String(next).length < 8) return { ok: false, error: 'The new password must be at least 8 characters.' };
    user.password = next;
    user.updatedOn = new Date().toISOString();
    save();
    return { ok: true };
  },

  toggleTwoFactor() {
    const user = Store.currentUser();
    if (!user) return false;
    user.twoFactorEnabled = !user.twoFactorEnabled;
    save();
    return user.twoFactorEnabled;
  },

  notifications() {
    return load().notifications;
  },

  setNotifications(prefs) {
    const s = load();
    s.notifications = prefs;
    save();
  },

  /* --------------------------------------------------------------------- news */

  news() {
    return load().news.slice().sort((a, b) => new Date(b.dateCreated) - new Date(a.dateCreated));
  },
};

window.Store = Store;
