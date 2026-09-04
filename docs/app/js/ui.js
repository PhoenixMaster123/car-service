/*
 * Shared page shell for the demo.
 *
 * The real application composes pages from Thymeleaf fragments
 * (fragments/navbar.html, fragments/sidebar.html, fragments/footer.html).
 * Static hosting has no template engine, so the same markup is injected here
 * instead — the classes and structure are copied from those fragments so the
 * application's own stylesheets style it unchanged.
 */

const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

const I18N = {
  en: {
    'nav.home': 'Home', 'nav.services': 'Services & Pricing', 'nav.more': 'More',
    'nav.about': 'About Us', 'nav.careers': 'Careers', 'nav.locations': 'Locations',
    'nav.news': 'News', 'nav.language': 'Language', 'nav.signIn': 'Sign In',
    'nav.register': 'Register', 'nav.myAccount': 'My Account', 'nav.profile': 'Profile',
    'nav.logout': 'Logout', 'nav.bookNow': 'Book Now',
    'hero.title': 'Drive Clean. Feel Fresh.',
    'hero.subtitle': 'Premium car wash and detailing with pro-grade equipment and eco-friendly products.',
    'hero.ctaPrimary': 'Book a Wash', 'hero.ctaSecondary': 'See Services',
    'services.title': 'Our Comprehensive Services',
    'hiw.title': 'How It Works',
  },
  de: {
    'nav.home': 'Startseite', 'nav.services': 'Leistungen & Preise', 'nav.more': 'Mehr',
    'nav.about': 'Über uns', 'nav.careers': 'Karriere', 'nav.locations': 'Standorte',
    'nav.news': 'Neuigkeiten', 'nav.language': 'Sprache', 'nav.signIn': 'Anmelden',
    'nav.register': 'Registrieren', 'nav.myAccount': 'Mein Konto', 'nav.profile': 'Profil',
    'nav.logout': 'Abmelden', 'nav.bookNow': 'Jetzt buchen',
    'hero.title': 'Sauber fahren. Frisch fühlen.',
    'hero.subtitle': 'Premium-Autowäsche und Aufbereitung mit professioneller Ausrüstung und umweltfreundlichen Produkten.',
    'hero.ctaPrimary': 'Wäsche buchen', 'hero.ctaSecondary': 'Leistungen ansehen',
    'services.title': 'Unser komplettes Leistungsangebot',
    'hiw.title': 'So funktioniert es',
  },
  bg: {
    'nav.home': 'Начало', 'nav.services': 'Услуги и цени', 'nav.more': 'Още',
    'nav.about': 'За нас', 'nav.careers': 'Кариери', 'nav.locations': 'Локации',
    'nav.news': 'Новини', 'nav.language': 'Език', 'nav.signIn': 'Вход',
    'nav.register': 'Регистрация', 'nav.myAccount': 'Моят профил', 'nav.profile': 'Профил',
    'nav.logout': 'Изход', 'nav.bookNow': 'Запази час',
    'hero.title': 'Карай чисто. Чувствай се свежо.',
    'hero.subtitle': 'Първокласно измиване и детайлинг с професионално оборудване и екологични продукти.',
    'hero.ctaPrimary': 'Запази измиване', 'hero.ctaSecondary': 'Виж услугите',
    'services.title': 'Нашите пълни услуги',
    'hiw.title': 'Как работи',
  },
};

const UI = {
  /* ------------------------------------------------------------ formatting */

  escape(value) {
    return String(value == null ? '' : value)
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
  },

  money(amount) {
    return '€' + Number(amount || 0).toFixed(2);
  },

  /** Matches the `MMM dd, yyyy` pattern the Thymeleaf templates use. */
  date(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return MONTHS[d.getMonth()] + ' ' + String(d.getDate()).padStart(2, '0') + ', ' + d.getFullYear();
  },

  time(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0');
  },

  dateTime(iso) {
    if (!iso) return 'N/A';
    return UI.date(iso) + ', ' + UI.time(iso);
  },

  /** `dd MMM yyyy`, as the admin tables render it. */
  dateAdmin(iso) {
    if (!iso) return 'N/A';
    const d = new Date(iso);
    return String(d.getDate()).padStart(2, '0') + ' ' + MONTHS[d.getMonth()] + ' ' + d.getFullYear();
  },

  capitalize(value) {
    const text = String(value || '').toLowerCase();
    return text.charAt(0).toUpperCase() + text.slice(1);
  },

  /* ------------------------------------------------------------------ i18n */

  lang() {
    return sessionStorage.getItem('demo-lang') || 'en';
  },

  setLang(code) {
    sessionStorage.setItem('demo-lang', code);
    UI.applyI18n();
  },

  t(key) {
    const dict = I18N[UI.lang()] || I18N.en;
    return dict[key] || I18N.en[key] || key;
  },

  applyI18n() {
    document.querySelectorAll('[data-i18n]').forEach((el) => {
      const key = el.getAttribute('data-i18n');
      const dict = I18N[UI.lang()] || I18N.en;
      if (dict[key]) el.textContent = dict[key];
    });
    document.documentElement.lang = UI.lang();
  },

  /* --------------------------------------------------------------- fragments */

  /** fragments/navbar.html */
  navbar() {
    const user = Store.currentUser();
    const profileHref = Store.isAdmin() ? 'admin-dashboard.html' : 'dashboard.html';
    const authBlock = user
      ? `<li class="dropdown user-dropdown">
           <a href="#" class="dropbtn">
             <i class="fa-solid fa-user-circle"></i> <span data-i18n="nav.myAccount">My Account</span> <i class="fa-solid fa-caret-down"></i>
           </a>
           <div class="dropdown-content">
             <a href="${profileHref}" data-i18n="nav.profile">Profile</a>
             <button type="button" class="logout-btn" data-demo-logout>
               <i class="fa-solid fa-right-from-bracket"></i> <span data-i18n="nav.logout">Logout</span>
             </button>
           </div>
         </li>`
      : `<li class="user-actions">
           <a href="index.html" data-i18n="nav.signIn">Sign In</a>
           <a href="register.html" class="register-btn" data-i18n="nav.register">Register</a>
         </li>`;

    return `
<nav class="navbar">
  <div class="navbar-container">
    <a href="home.html" class="brand-name">Haris Car Care</a>

    <div class="menu-toggle" id="mobile-menu">
      <span class="bar"></span><span class="bar"></span><span class="bar"></span>
    </div>

    <ul class="nav-links" id="nav-links">
      <li><a href="home.html" data-i18n="nav.home">Home</a></li>
      <li><a href="services.html" data-i18n="nav.services">Services &amp; Pricing</a></li>
      <li class="dropdown">
        <a href="#" class="dropbtn"><span data-i18n="nav.more">More</span> <i class="fa-solid fa-caret-down"></i></a>
        <div class="dropdown-content">
          <a href="about.html" data-i18n="nav.about">About Us</a>
          <a href="careers.html" data-i18n="nav.careers">Careers</a>
          <a href="locations.html" data-i18n="nav.locations">Locations</a>
          <a href="news.html" data-i18n="nav.news">News</a>
        </div>
      </li>
      <li class="dropdown language-switcher">
        <a href="#" class="dropbtn"><i class="fa-solid fa-globe"></i> <span data-i18n="nav.language">Language</span></a>
        <div class="dropdown-content">
          <a href="#" data-demo-lang="en">English</a>
          <a href="#" data-demo-lang="de">Deutsch</a>
          <a href="#" data-demo-lang="bg">Български</a>
        </div>
      </li>
      ${authBlock}
      <li class="theme-toggle-wrapper mobile-only">
        <div class="theme-toggle"><i class="fa-solid fa-sun"></i><i class="fa-solid fa-moon"></i><div class="toggle-circle"></div></div>
      </li>
      <li class="mobile-only"><a href="booking.html" class="cta-button" data-i18n="nav.bookNow">Book Now</a></li>
    </ul>

    <div class="theme-toggle" id="theme-toggle-desktop">
      <i class="fa-solid fa-sun"></i><i class="fa-solid fa-moon"></i><div class="toggle-circle"></div>
    </div>

    <a href="booking.html" class="cta-button desktop-only" data-i18n="nav.bookNow">Book Now</a>
  </div>
</nav>`;
  },

  /** fragments/sidebar.html */
  sidebar(active) {
    const link = (href, key, icon, label) => `
      <li><a href="${href}" class="${active === key ? 'active' : ''}"><i class="fa-solid ${icon}"></i> ${label}</a></li>`;
    return `
<aside class="sidebar">
  <div class="sidebar-brand"><a href="home.html" class="brand-name">Haris Car Care</a></div>
  <ul class="sidebar-links">
    ${link('dashboard.html', 'dashboard', 'fa-table-columns', 'Dashboard')}
    ${link('vehicles.html', 'vehicles', 'fa-car-side', 'My Vehicles')}
    ${link('my-bookings.html', 'bookings', 'fa-calendar-check', 'My Bookings')}
    ${link('invoices.html', 'invoices', 'fa-file-invoice-dollar', 'Invoices')}
    ${link('settings.html', 'settings', 'fa-gear', 'Settings')}
    <li><a href="home.html"><i class="fa-solid fa-house"></i> Back to Site</a></li>
    <li><a href="#" data-demo-logout><i class="fa-solid fa-right-from-bracket"></i> Logout</a></li>
  </ul>
  <div class="sidebar-footer">
    <div class="theme-toggle" id="theme-toggle-desktop">
      <i class="fa-solid fa-sun"></i><i class="fa-solid fa-moon"></i><div class="toggle-circle"></div>
    </div>
  </div>
</aside>`;
  },

  /** fragments/admin-sidebar.html */
  adminSidebar(active) {
    const link = (href, key, icon, label) => `
      <li><a href="${href}" class="${active === key ? 'active' : ''}"><i class="fa-solid ${icon}"></i> ${label}</a></li>`;
    return `
<aside class="sidebar">
  <a href="home.html" class="sidebar-brand">Haris Car Care</a>
  <ul class="sidebar-nav">
    ${link('admin-dashboard.html', 'dashboard', 'fa-gauge-high', 'Dashboard')}
    ${link('admin-users.html', 'users', 'fa-users', 'Users')}
    ${link('admin-services.html', 'services', 'fa-car', 'Services')}
    ${link('admin-bookings.html', 'bookings', 'fa-calendar-check', 'Bookings')}
    ${link('admin-repairs.html', 'repairs', 'fa-screwdriver-wrench', 'Repairs')}
    ${link('admin-reports.html', 'reports', 'fa-chart-line', 'Reports')}
    ${link('admin-settings.html', 'settings', 'fa-gear', 'Settings')}
    <li><a href="home.html"><i class="fa-solid fa-house"></i> Back to Site</a></li>
    <li><a href="#" data-demo-logout><i class="fa-solid fa-right-from-bracket"></i> Logout</a></li>
  </ul>
  <div class="sidebar-footer">
    <div class="theme-toggle" id="theme-toggle">
      <i class="fa-solid fa-sun"></i><i class="fa-solid fa-moon"></i><div class="toggle-circle"></div>
    </div>
  </div>
</aside>`;
  },

  /** fragments/footer.html :: site-footer */
  footer() {
    return `
<footer class="footer">
  <div class="footer-container">
    <div class="footer-col">
      <h4>Haris Car Care</h4>
      <p>Your one-stop shop for premium car washing, detailing, and expert mechanical repairs. Drive clean, feel fresh.</p>
      <div class="social-links">
        <a href="#" aria-label="Facebook"><i class="fa-brands fa-facebook-f"></i></a>
        <a href="#" aria-label="Instagram"><i class="fa-brands fa-instagram"></i></a>
        <a href="#" aria-label="Twitter"><i class="fa-brands fa-x-twitter"></i></a>
      </div>
    </div>
    <div class="footer-col">
      <h4>Quick Links</h4>
      <ul>
        <li><a href="home.html">Home</a></li>
        <li><a href="services.html">Services &amp; Pricing</a></li>
        <li><a href="about.html">About Us</a></li>
        <li><a href="booking.html">Book Now</a></li>
        <li><a href="careers.html">Careers</a></li>
      </ul>
    </div>
    <div class="footer-col">
      <h4>Our Services</h4>
      <ul>
        <li><a href="services.html">Car Washing</a></li>
        <li><a href="services.html">Carpet Cleaning</a></li>
        <li><a href="services.html">Oil Change</a></li>
        <li><a href="services.html">Tire Change</a></li>
        <li><a href="services.html">A/C Refill</a></li>
        <li><a href="services.html">DPF Filter Cleaning</a></li>
      </ul>
    </div>
    <div class="footer-col">
      <h4>Contact Us</h4>
      <ul>
        <li><i class="fa-solid fa-location-dot"></i> Maria Luiza Blvd, Ruse, Bulgaria</li>
        <li><i class="fa-solid fa-phone"></i> +359 00 000 000</li>
        <li><i class="fa-solid fa-envelope"></i> hariscarcare@gmail.com</li>
        <li><i class="fa-solid fa-clock"></i> Mon–Sat: 8:00 AM – 7:00 PM</li>
      </ul>
    </div>
  </div>
  <div class="footer-bottom">
    <p>&copy; ${new Date().getFullYear()} Haris Car Care. All Rights Reserved.</p>
  </div>
</footer>`;
  },

  /** fragments/footer.html :: chatbot-widget */
  chatbot() {
    return `
<button class="chat-toggle" id="chat-toggle" aria-label="Toggle Chat">
  <i class="fa-solid fa-comment-dots" id="chat-icon-open"></i>
  <i class="fa-solid fa-xmark" id="chat-icon-close"></i>
</button>
<div class="chat-widget" id="chat-widget">
  <div class="chat-header">
    <h3>Haris AI Assistant</h3>
    <button class="chat-close-btn" id="chat-close-btn" aria-label="Close Chat">&times;</button>
  </div>
  <div class="chat-body" id="chat-body">
    <div class="chat-message bot"><p>Hello! How can I help you with your car care questions today?</p></div>
  </div>
  <form class="chat-footer" id="chat-form">
    <label for="chat-input"></label>
    <input type="text" id="chat-input" placeholder="Ask a question..." autocomplete="off">
    <button type="submit" aria-label="Send Message"><i class="fa-solid fa-paper-plane"></i></button>
  </form>
</div>`;
  },

  /* ----------------------------------------------------------------- banner */

  banner() {
    return `
<div class="demo-banner">
  <span class="demo-banner-tag">Demo</span>
  <span class="demo-banner-text">
    Static build of the Spring Boot app — no backend. Changes live in this tab only.
  </span>
  <button type="button" class="demo-banner-reset" data-demo-reset>Reset data</button>
</div>`;
  },

  /* ------------------------------------------------------------------ mount */

  /**
   * Injects the shell for a page and wires the behaviour that the app's own
   * scripts provide server-side (theme, mobile nav, chatbot, logout).
   *
   * @param {{shell: string, active: string, guard: string}} options
   */
  mount(options) {
    const opts = options || {};

    if (opts.guard === 'user' && !Store.isAuthenticated()) {
      window.location.replace('index.html');
      return false;
    }
    if (opts.guard === 'admin' && !Store.isAdmin()) {
      window.location.replace(Store.isAuthenticated() ? 'dashboard.html' : 'index.html');
      return false;
    }

    document.body.insertAdjacentHTML('afterbegin', UI.banner());
    UI.measureBanner();

    const slot = (selector, html) => {
      const el = document.querySelector(selector);
      if (el) el.outerHTML = html;
    };
    slot('[data-slot="navbar"]', UI.navbar());
    slot('[data-slot="sidebar"]', UI.sidebar(opts.active));
    slot('[data-slot="admin-sidebar"]', UI.adminSidebar(opts.active));
    slot('[data-slot="footer"]', UI.footer());
    slot('[data-slot="chatbot"]', UI.chatbot());

    UI.wireTheme();
    UI.wireNav();
    UI.wireChatbot();
    UI.wireLang();
    UI.applyI18n();

    document.querySelectorAll('[data-demo-logout]').forEach((el) => {
      el.addEventListener('click', (event) => {
        event.preventDefault();
        Store.signOut();
        window.location.href = 'index.html';
      });
    });

    document.querySelectorAll('[data-demo-reset]').forEach((el) => {
      el.addEventListener('click', () => {
        Store.reset();
        window.location.href = 'index.html';
      });
    });

    return true;
  },

  /**
   * Publishes the banner's height so the fixed-position sidebars can be offset
   * by it. Re-measured on resize, where the banner wraps to two lines.
   */
  measureBanner() {
    const banner = document.querySelector('.demo-banner');
    if (!banner) return;
    const apply = () => document.documentElement.style
      .setProperty('--demo-banner-h', banner.offsetHeight + 'px');
    apply();
    window.addEventListener('resize', apply);
  },

  /* ---------------------------------------------------------------- wiring */

  /** Same contract as component/theme-toggle/toggleSwitch.js. */
  wireTheme() {
    const apply = (theme) => {
      document.body.classList.toggle('light', theme === 'light');
      localStorage.setItem('theme', theme);
    };
    document.querySelectorAll('.theme-toggle').forEach((toggle) => {
      toggle.addEventListener('click', () => {
        apply(document.body.classList.contains('light') ? 'dark' : 'light');
        document.dispatchEvent(new CustomEvent('demo:themechange'));
      });
    });
    apply(localStorage.getItem('theme') || 'dark');
  },

  wireNav() {
    const toggle = document.getElementById('mobile-menu');
    const links = document.getElementById('nav-links');
    if (toggle && links) {
      toggle.addEventListener('click', () => {
        links.classList.toggle('active');
        toggle.classList.toggle('is-active');
      });
    }
    /* Dropdowns open on hover via CSS; on touch a tap has to do it. */
    document.querySelectorAll('.dropdown > .dropbtn').forEach((btn) => {
      btn.addEventListener('click', (event) => {
        if (window.matchMedia('(hover: hover)').matches) return;
        event.preventDefault();
        btn.parentElement.classList.toggle('open');
      });
    });
  },

  wireLang() {
    document.querySelectorAll('[data-demo-lang]').forEach((el) => {
      el.addEventListener('click', (event) => {
        event.preventDefault();
        UI.setLang(el.getAttribute('data-demo-lang'));
      });
    });
  },

  /**
   * The real widget posts to /api/chat, which asks Gemini. There is no server
   * here, so the demo answers from the seeded catalogue instead.
   */
  wireChatbot() {
    const toggle = document.getElementById('chat-toggle');
    const widget = document.getElementById('chat-widget');
    const closeBtn = document.getElementById('chat-close-btn');
    const form = document.getElementById('chat-form');
    const input = document.getElementById('chat-input');
    const body = document.getElementById('chat-body');
    if (!toggle || !widget || !form) return;

    const open = () => { widget.classList.add('active'); toggle.classList.add('active'); };
    const close = () => { widget.classList.remove('active'); toggle.classList.remove('active'); };

    toggle.addEventListener('click', () => {
      if (widget.classList.contains('active')) close(); else open();
    });
    if (closeBtn) closeBtn.addEventListener('click', close);

    const say = (text, who) => {
      const div = document.createElement('div');
      div.className = 'chat-message ' + who;
      div.innerHTML = '<p>' + UI.escape(text) + '</p>';
      body.appendChild(div);
      body.scrollTop = body.scrollHeight;
    };

    form.addEventListener('submit', (event) => {
      event.preventDefault();
      const question = input.value.trim();
      if (!question) return;
      say(question, 'user');
      input.value = '';
      setTimeout(() => say(UI.answer(question), 'bot'), 500);
    });
  },

  /**
   * Picks the service a question is about: a full name match first, otherwise
   * the service sharing the most significant words with the question.
   */
  matchService(question) {
    const q = question.toLowerCase();
    const services = Store.services();

    const exact = services.find((sv) => q.includes(sv.name.toLowerCase()));
    if (exact) return exact;

    const stop = new Set(['and', 'the', 'for', 'with', 'change', 'service', 'car', 'much', 'how',
      'cost', 'costs', 'price', 'does', 'what', 'your', 'you', 'is', 'a', 'an', 'of', 'my']);
    const asked = q.split(/[^a-z]+/).filter((w) => w.length > 2 && !stop.has(w));
    if (asked.length === 0) return null;

    let best = null;
    let bestScore = 0;
    services.forEach((sv) => {
      const words = sv.name.toLowerCase().split(/[^a-z]+/);
      const score = asked.filter((w) => words.some((n) => n.startsWith(w) || w.startsWith(n))).length;
      if (score > bestScore) {
        bestScore = score;
        best = sv;
      }
    });
    return bestScore > 0 ? best : null;
  },

  answer(question) {
    const q = question.toLowerCase();
    const services = Store.services();

    const match = UI.matchService(question);
    if (match) {
      return `${match.name} costs ${UI.money(match.basePrice)} and takes about ${match.duration} minutes. `
        + 'You can add it to your cart from the Services page.';
    }
    if (q.includes('price') || q.includes('cost') || q.includes('how much')) {
      const cheapest = services.reduce((a, b) => (a.basePrice < b.basePrice ? a : b));
      return `Our catalogue runs from ${UI.money(cheapest.basePrice)} (${cheapest.name}) upwards. `
        + 'The Services page lists every service with its price and duration.';
    }
    if (q.includes('open') || q.includes('hour') || q.includes('time')) {
      return 'We are open Monday to Saturday, 08:00–19:00, and Sunday 10:00–16:00.';
    }
    if (q.includes('where') || q.includes('location') || q.includes('address')) {
      return 'You will find us on Maria Luiza Blvd in Ruse, Bulgaria. The Locations page has a map.';
    }
    if (q.includes('book') || q.includes('appointment')) {
      return 'Pick your services on the Services page, then open Book Now to choose a date, a vehicle '
        + 'and a payment method.';
    }
    if (q.includes('invoice') || q.includes('pdf') || q.includes('receipt')) {
      return 'Every paid booking raises an invoice under Invoices in your account, and each one can be '
        + 'downloaded as a PDF.';
    }
    return 'In the live application this goes to Gemini through the /api/chat endpoint. This static demo '
      + 'answers from the seeded service catalogue, so try asking about a service, our prices, '
      + 'opening hours or how to book.';
  },

  /** Renders a dismissible alert into a page's flash slot. */
  flash(target, type, text) {
    const host = typeof target === 'string' ? document.querySelector(target) : target;
    if (!host) return;
    host.innerHTML = `<div class="demo-alert demo-alert-${type}">
      <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i>
      <span>${UI.escape(text)}</span>
    </div>`;
    host.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  },

  /** Drains a flash stored before a navigation and shows it. */
  showPendingFlash(target) {
    const flash = Store.takeFlash();
    if (flash) UI.flash(target, flash.type, flash.text);
  },
};

window.UI = UI;
