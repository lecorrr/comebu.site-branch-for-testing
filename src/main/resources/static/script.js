document.addEventListener("DOMContentLoaded", () => {
  const burger = document.getElementById("burger");
  const nav = document.getElementById("nav");
  const profileTab = document.getElementById("profile-tab");
  const ordersTab = document.getElementById("orders-tab");
  const profileContent = document.getElementById("profile-content");
  const ordersContent = document.getElementById("orders-content");

  if (profileTab && ordersTab && profileContent && ordersContent) {
    profileTab.addEventListener("click", () => {
      profileTab.classList.add("active");
      ordersTab.classList.remove("active");
      profileContent.classList.remove("hidden");
      ordersContent.classList.add("hidden");
    });

    ordersTab.addEventListener("click", () => {
      ordersTab.classList.add("active");
      profileTab.classList.remove("active");
      ordersContent.classList.remove("hidden");
      profileContent.classList.add("hidden");
    });
  }

  if (burger && nav) {
    burger.addEventListener("click", () => {
      nav.classList.toggle("active");
    });
  }

  const searchBtn = document.getElementById("search-btn");
  const searchBox = document.getElementById("search-box");
  const searchInput = document.getElementById("search-input");

  if (searchBtn && searchBox) {
    searchBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      searchBox.classList.toggle("active");
      searchBox.setAttribute(
        "aria-hidden",
        searchBox.classList.contains("active") ? "false" : "true"
      );
      if (searchBox.classList.contains("active") && searchInput) {
        setTimeout(() => searchInput.focus(), 200);
      }
    });

    document.addEventListener("click", (e) => {
      if (!searchBox.contains(e.target) && !searchBtn.contains(e.target)) {
        searchBox.classList.remove("active");
        searchBox.setAttribute("aria-hidden", "true");
      }
    });

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        searchBox.classList.remove("active");
        searchBox.setAttribute("aria-hidden", "true");
      }
    });
  }

  const cartSidebar = document.getElementById("cart-sidebar");
  const cartContent = document.querySelector(".cart-content");
  const cartBtn = document.querySelector(".icons-shopping img");
  const closeCart = document.getElementById("close-cart");
  const checkoutBtn = document.getElementById("checkout-btn");

  // load saved cart from localStorage (persist between pages)
  let cartItems = loadCart();

  if (cartBtn && cartSidebar) {
    cartBtn.addEventListener("click", () => {
      cartSidebar.classList.toggle("active");
    });
  }
  if (closeCart) {
    closeCart.addEventListener("click", () => {
      cartSidebar.classList.remove("active");
    });
  }

  // save cart to localStorage
  function saveCart() {
    try {
      localStorage.setItem("cartItems", JSON.stringify(cartItems));
    } catch (e) {
      console.warn("Could not save cart to localStorage", e);
    }
  }

  // load cart from localStorage
  function loadCart() {
    try {
      const raw = localStorage.getItem("cartItems");
      return raw ? JSON.parse(raw) : [];
    } catch (e) {
      console.warn("Could not load cart from localStorage", e);
      return [];
    }
  }

  function addToCart(name, price, image, description) {
    const existingItem = cartItems.find((i) => i.name === name);
    if (existingItem) {
      existingItem.quantity++;
    } else {
      cartItems.push({
        name,
        price: parseFloat(price),
        image,
        quantity: 1,
        description: description || ""
      });
    }
    saveCart();
    renderCart();
  }

  window.addToCart = addToCart;

  function updateQuantity(name, change) {
    const item = cartItems.find((i) => i.name === name);
    if (item) {
      item.quantity += change;
      if (item.quantity <= 0) {
        cartItems = cartItems.filter((i) => i.name !== name);
      }
      saveCart();
      renderCart();
    }
  }

  function getTotalPrice() {
    return cartItems
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
      .toFixed(2);
  }

  function renderCart() {
    if (!cartContent) return;

    const cartTotal = document.querySelector(".cart-total");
    if (!cartTotal) {
      console.warn(
        "Елемент .cart-total не знайдено — пропускаємо оновлення кошика"
      );
      return;
    }

    if (cartItems.length === 0) {
      cartContent.innerHTML = "<p>Ваша корзина поки що пуста</p>";
      cartTotal.innerHTML = "";
      return;
    }

    cartContent.innerHTML = cartItems
      .map(
        (item) => `
        <div class="cart-item">
          <img src="${item.image}" alt="${item.name}" width="45" height="45">
          <div class="cart-item-info">
            <strong>${item.name}</strong><br>
            <span>${item.price.toFixed(2)} ₴ × ${item.quantity}</span>
            <button class="cart-description-btn" data-name="${item.name}">!</button>
          </div>
          <div class="cart-controls">
            <button class="qty-btn" data-name="${
              item.name
            }" data-action="minus">−</button>
            <button class="qty-btn" data-name="${
              item.name
            }" data-action="plus">+</button>
            <button class="remove-item" data-name="${item.name}">✖</button>
          </div>
        </div>
      `
      )
      .join("");


    cartTotal.innerHTML = `
      <div class="cart-summary">
        <strong>Загальна сума:</strong>
        <span>${getTotalPrice()} ₴</span>
      </div>
    `;

    document.querySelectorAll(".qty-btn").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const name = e.target.dataset.name;
        const action = e.target.dataset.action;
        if (action === "plus") updateQuantity(name, 1);
        else if (action === "minus") updateQuantity(name, -1);
      });
    });

    document.querySelectorAll(".remove-item").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const name = e.target.dataset.name;
        cartItems = cartItems.filter((item) => item.name !== name);
        saveCart();
        renderCart();
      });
    });

    document.querySelectorAll(".cart-description-btn").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const name = e.target.dataset.name;
        const item = cartItems.find((i) => i.name === name);
        if (item && item.description) {
          alert(item.description);
        }
      });
    });
  }

  if (checkoutBtn) {
    checkoutBtn.addEventListener("click", () => {
      if (cartItems.length === 0) {
        alert("Ваша корзина порожня 😕");
        return;
      }
      alert(`Ваше замовлення на суму ${getTotalPrice()} ₴ прийнято!`);
      cartItems = [];
      saveCart();
      renderCart();
    });
  }

  // render cart initially from loaded state
  renderCart();

  /* ---------- Edit-field modal logic ---------- */
  const modal = document.getElementById("edit-modal");
  const modalTitle = document.getElementById("modal-title");
  const modalInput = document.getElementById("modal-input");
  const modalRowSingle = document.getElementById("modal-row-single");
  const modalRowPassword = document.getElementById("modal-row-password");
  const modalOld = document.getElementById("modal-old");
  const modalNew = document.getElementById("modal-new");
  const modalConfirm = document.getElementById("modal-confirm");
  const modalError = document.getElementById("modal-error");
  const modalSave = document.getElementById("modal-save");
  const modalCancel = document.getElementById("modal-cancel");
  const modalClose = document.querySelector(".modal-close");
  let currentTargetItem = null;
  let currentField = null;

  // Open modal with context from clicked edit button
  document.querySelectorAll(".edit-btn").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const field = btn.dataset.field || "field";
      const item = btn.closest(".account-item");
      currentTargetItem = item;
      currentField = field;

      if (modalError) {
        modalError.style.display = "none";
      }

      modalTitle.textContent =
        {
          name: "Редагувати ім'я",
          email: "Редагувати ел. пошту",
          phone: "Редагувати номер телефону",
          password: "Змінити пароль",
          payment: "Редагувати способи оплати",
          address: "Редагувати адресу",
        }[field] || "Редагувати";

      if (field === "password") {
        modalRowSingle?.classList.add("hidden");
        modalRowPassword?.classList.remove("hidden");
        if (modalOld) modalOld.value = "";
        if (modalNew) modalNew.value = "";
        if (modalConfirm) modalConfirm.value = "";
        modalOld?.focus();
      } else {
        modalRowPassword?.classList.add("hidden");
        modalRowSingle?.classList.remove("hidden");
        const textNode = item.querySelector(".item-info div p");
        if (modalInput) {
          modalInput.type = "text";
          modalInput.value = textNode ? textNode.textContent.trim() : "";
          modalInput.focus();
        }
      }
      openModal();
    });
  });

  function openModal() {
    if (!modal) return;
    modal.classList.remove("hidden");
    modal.setAttribute("aria-hidden", "false");
  }
  function closeModal() {
    if (!modal) return;
    modal.classList.add("hidden");
    modal.setAttribute("aria-hidden", "true");
    currentTargetItem = null;
    currentField = null;
    if (modalError) modalError.style.display = "none";
  }

  if (modalSave) {
    modalSave.addEventListener("click", () => {
      if (!currentTargetItem || !currentField) return closeModal();
      const infoDiv = currentTargetItem.querySelector(".item-info div");
      if (currentField === "password") {
        const oldVal = modalOld?.value.trim();
        const newVal = modalNew?.value.trim();
        const conf = modalConfirm?.value.trim();
        // basic validation
        if (!oldVal || !newVal || !conf) {
          if (modalError) {
            modalError.textContent = "Всі поля пароля повинні бути заповнені.";
            modalError.style.display = "block";
          }
          return;
        }
        if (newVal !== conf) {
          if (modalError) {
            modalError.textContent =
              "Новий пароль та підтвердження не співпадають.";
            modalError.style.display = "block";
          }
          return;
        }
        // success: show masked password
        if (infoDiv) {
          let p = infoDiv.querySelector("p");
          if (!p) {
            p = document.createElement("p");
            infoDiv.appendChild(p);
          }
          p.textContent = "••••••••";
        }
        closeModal();
      } else {
        const newVal = modalInput?.value.trim();
        if (infoDiv) {
          let p = infoDiv.querySelector("p");
          if (!p) {
            p = document.createElement("p");
            infoDiv.appendChild(p);
          }
          p.textContent =
            newVal || (currentField === "payment" ? "Не вказано" : "");
        }
        closeModal();
      }
    });
  }

  [modalCancel, modalClose].forEach((el) => {
    if (!el) return;
    el.addEventListener("click", (e) => {
      e.preventDefault();
      closeModal();
    });
  });

  if (modal) {
    modal.addEventListener("click", (e) => {
      if (e.target === modal) closeModal();
    });
  }

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeModal();
  });

  // === ДИНАМІЧНЕ ЗАВАНТАЖЕННЯ ТОВАРІВ З БЕКЕНДУ + АНІМАЦІЯ ЗАВАНТАЖЕННЯ ===
  const catalogEl = document.getElementById("catalog");
  let loadingAnimation = null;
  let loadingEl = null;

  function startLoadingAnimation() {
    if (!catalogEl) return;

    // if (!loadingEl) {
    //   loadingEl = document.createElement("div");
    //   loadingEl.id = "loading-animation";
    //   loadingEl.className = "loading";
    //   loadingEl.innerHTML = `
    //     <div class="circle"></div>
    //     <div class="circle"></div>
    //     <div class="circle"></div>
    //     <div class="circle"></div>
    //     <div class="circle"></div>
    //   `;
    //   if (catalogEl.parentNode) {
    //     catalogEl.parentNode.insertBefore(loadingEl, catalogEl);
    //   }
    // }

    if (window.anime && !loadingAnimation) {
      const isPortrait = window.matchMedia("(orientation: portrait)").matches;
      loadingAnimation = window.anime({
        targets: "#loading-animation .circle",
        scale: [
          { value: 1, duration: 0 },
          { value: 1.6, duration: 400 },
          { value: 1, duration: 400 },
        ],
        delay: window.anime.stagger(150),
        easing: "easeInOutQuad",
        loop: true,
      });
    }
  }

  function stopLoadingAnimation() {
    if (loadingAnimation) {
      loadingAnimation.pause();
      loadingAnimation = null;
    }
    if (loadingEl && loadingEl.parentNode) {
      loadingEl.parentNode.removeChild(loadingEl);
      loadingEl = null;
    }
  }

  if (catalogEl) {
    loadProducts();
  }

  async function loadProducts() {
    const API_URL = "/api/products";

    startLoadingAnimation();
    catalogEl.innerHTML = "";

    try {
      const res = await fetch(API_URL, {
        headers: { Accept: "application/json" },
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const products = await res.json();
      renderProducts(products);
    } catch (err) {
      console.error("Помилка завантаження продуктів:", err);
      stopLoadingAnimation();
      catalogEl.innerHTML =
        '<p class="error-wrap">Не вдалося завантажити товари</p>';
    }
  }

  function renderProducts(products) {
    stopLoadingAnimation();

    if (!Array.isArray(products) || products.length === 0) {
      catalogEl.innerHTML =
        '<p style="text-align:center;opacity:.7">Товарів поки немає</p>';
      return;
    }

    catalogEl.innerHTML = products
      .map((p) => {
        const name = escapeHtml(p.name ?? "Без назви");
        const weight = escapeHtml(p.weight ?? "");
        const description = escapeHtml(p.description ?? "");
        const price = Number(p.price ?? 0);
        const image = p.image || "images/no-image.png";
        const id = p.id ?? 0;

        return `
      <div class="product-card" data-id="${id}">
        <div class="product-img">
          <img src="${image}" alt="${name}">
        </div>
        <div class="product-info">
          <div>
            <b>${name}</b>
            <div class="product-weight">${weight}</div>
          </div>
          <div class="product-bottom">
            <span class="product-price">${price.toFixed(2)} ₴</span>
            <button class="add-btn" data-id="${id}" data-name="${name}" data-price="${price}" data-image="${image}" data-description="${description}">
              <span>+</span> Додати
            </button>
          </div>
          <div class="admin-controls admin-only">
            <button class="admin-edit-btn" data-id="${id}">Редагувати</button>
            <button class="admin-delete-btn" data-id="${id}">Видалити</button>
          </div>
        </div>
      </div>
    `;
      })
      .join("");


    // Делегування кліків: працює і для щойно згенерованих кнопок
    catalogEl.addEventListener("click", async (e) => {
      const addBtn = e.target.closest(".add-btn");
      if (addBtn) {
        const name = addBtn.dataset.name;
        const price = addBtn.dataset.price;
        const image = addBtn.dataset.image || "images/no-image.png";
        const description = addBtn.dataset.description || "";
        if (typeof window.addToCart === "function") {
          window.addToCart(name, price, image, description);
        }
        return;
      }

      const editBtn = e.target.closest(".admin-edit-btn");
      if (editBtn) {
        const id = editBtn.dataset.id;
        const token = localStorage.getItem("authToken");
        if (!token) {
          alert("Немає доступу");
          return;
        }
        try {
          const res = await fetch(`/api/products/${id}`);
          if (!res.ok) {
            alert("Не вдалося завантажити товар");
            return;
          }
          const product = await res.json();
          const name = prompt("Назва", product.name || "");
          if (!name) {
            return;
          }
          const description = prompt("Опис", product.description || "") || "";
          const weight = prompt("Вага", product.weight || "") || "";
          const priceStr = prompt("Ціна", String(product.price ?? 0));
          if (!priceStr) {
            return;
          }
          const price = parseFloat(priceStr);
          const image = prompt("URL зображення", product.image || "") || "";
          const body = { name, description, weight, price, image };
          const putRes = await fetch(`/api/products/${id}`, {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + token
            },
            body: JSON.stringify(body)
          });
          if (!putRes.ok) {
            alert("Не вдалося зберегти товар");
            return;
          }
          await loadProducts();
        } catch (err) {
          alert("Помилка мережі");
        }
        return;
      }

      const deleteBtn = e.target.closest(".admin-delete-btn");
      if (deleteBtn) {
        const id = deleteBtn.dataset.id;
        const token = localStorage.getItem("authToken");
        if (!token) {
          alert("Немає доступу");
          return;
        }
        if (!confirm("Видалити товар?")) {
          return;
        }
        try {
          const res = await fetch(`/api/products/${id}`, {
            method: "DELETE",
            headers: {
              Authorization: "Bearer " + token
            }
          });
          if (!res.ok) {
            alert("Не вдалося видалити товар");
            return;
          }
          await loadProducts();
        } catch (err) {
          alert("Помилка мережі");
        }
      }
    });
  }

  function getAuthUser() {
    try {
      const raw = localStorage.getItem("authUser");
      return raw ? JSON.parse(raw) : null;
    } catch (e) {
      return null;
    }
  }

  function getAuthToken() {
    const token = localStorage.getItem("authToken");
    return token || "";
  }

  function saveAuth(token, user) {
    if (token) {
      localStorage.setItem("authToken", token);
    }
    if (user) {
      localStorage.setItem("authUser", JSON.stringify(user));
    }
    if (user && user.admin) {
      document.body.classList.add("admin-user");
    } else {
      document.body.classList.remove("admin-user");
    }
  }

  const existingUser = getAuthUser();
  if (existingUser && existingUser.admin) {
    document.body.classList.add("admin-user");
  }

  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const emailInput = document.getElementById("loginEmail");
      const passwordInput = document.getElementById("loginPassword");
      const email = emailInput ? emailInput.value.trim() : "";
      const password = passwordInput ? passwordInput.value : "";
      if (!email || !password) {
        alert("Введіть email і пароль");
        return;
      }
      try {
        const res = await fetch("/api/auth/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (!res.ok) {
          alert(data.error || "Помилка входу");
          return;
        }
        saveAuth(data.token, data.user);
        window.location.href = "/shop.html";
      } catch (err) {
        alert("Помилка мережі");
      }
    });
  }

  const registerForm = document.getElementById("registerForm");
  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const emailInput = document.getElementById("regEmail");
      const passwordInput = document.getElementById("regPassword");
      const email = emailInput ? emailInput.value.trim() : "";
      const password = passwordInput ? passwordInput.value : "";
      if (!email || !password) {
        alert("Введіть email і пароль");
        return;
      }
      try {
        const res = await fetch("/api/auth/register", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (!res.ok) {
          alert(data.error || "Помилка реєстрації");
          return;
        }
        saveAuth(data.token, data.user);
        window.location.href = "/shop.html";
      } catch (err) {
        alert("Помилка мережі");
      }
    });
  }

  const adminPanel = document.getElementById("admin-panel");
  const adminProductForm = document.getElementById("admin-product-form");
  if (adminPanel) {
    const user = getAuthUser();
    if (!user || !user.admin) {
      adminPanel.classList.add("hidden");
    } else {
      adminPanel.classList.remove("hidden");
    }
  }

  if (adminProductForm) {
    adminProductForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const user = getAuthUser();
      if (!user || !user.admin) {
        alert("Немає доступу");
        return;
      }
      const token = getAuthToken();
      if (!token) {
        alert("Немає доступу");
        return;
      }
      const nameInput = document.getElementById("admin-product-name");
      const descriptionInput = document.getElementById("admin-product-description");
      const weightInput = document.getElementById("admin-product-weight");
      const priceInput = document.getElementById("admin-product-price");
      const imageInput = document.getElementById("admin-product-image");

      const name = nameInput ? nameInput.value.trim() : "";
      const description = descriptionInput ? descriptionInput.value.trim() : "";
      const weight = weightInput ? weightInput.value.trim() : "";
      const priceValue = priceInput ? priceInput.value : "";
      const image = imageInput ? imageInput.value.trim() : "";

      if (!name || !priceValue) {
        alert("Заповніть назву і ціну");
        return;
      }

      const price = parseFloat(priceValue);

      try {
        const res = await fetch("/api/products", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token
          },
          body: JSON.stringify({
            name,
            description,
            weight,
            price,
            image
          })
        });
        if (!res.ok) {
          alert("Не вдалося створити товар");
          return;
        }
        adminProductForm.reset();
        await loadProducts();
      } catch (err) {
        alert("Помилка мережі");
      }
    });
  }

  function escapeHtml(str) {
    return String(str).replace(
      /[&<>"']/g,
      (s) =>
        ({
          "&": "&amp;",
          "<": "&lt;",
          ">": "&gt;",
          '"': "&quot;",
          "'": "&#39;",
        }[s])
    );
  }
});

// 🔎 Пошук по товарах (актуальні картки, навіть динамічні)
const searchInputShop = document.getElementById("search-input");
const catalog = document.querySelector(".catalog");

if (searchInputShop && catalog) {
  const noResults = document.createElement("p");
  noResults.textContent = "Товарів не знайдено 😕";
  noResults.style.textAlign = "center";
  noResults.style.color = "#555";
  noResults.style.display = "none";
  catalog.appendChild(noResults);

  searchInputShop.addEventListener("input", () => {
    const query = searchInputShop.value.toLowerCase().trim();
    let found = 0;

    const cards = catalog.querySelectorAll(".product-card");

    cards.forEach((card) => {
      const titleEl = card.querySelector("b, strong");
      const title = titleEl ? titleEl.textContent.toLowerCase() : "";
      if (title.includes(query)) {
        card.style.display = "flex";
        found++;
      } else {
        card.style.display = "none";
      }
    });

    noResults.style.display = found === 0 ? "block" : "none";
  });

  searchInputShop.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      searchInputShop.value = "";
      const cards = catalog.querySelectorAll(".product-card");
      cards.forEach((card) => (card.style.display = "flex"));
      noResults.style.display = "none";
    }
  });
}
