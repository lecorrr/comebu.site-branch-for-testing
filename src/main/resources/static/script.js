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

    function addToCart(name, price, image) {
        const existingItem = cartItems.find((i) => i.name === name);
        if (existingItem) {
            existingItem.quantity++;
        } else {
            cartItems.push({
                name,
                price: parseFloat(price),
                image,
                quantity: 1,
            });
        }
        saveCart();
        renderCart();
    }

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

        if (cartItems.length === 0) {
            cartContent.innerHTML = "<p>Ваша корзина поки що пуста</p>";
            document.querySelector(".cart-total").innerHTML = "";
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

        document.querySelector(".cart-total").innerHTML = `
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
    }

    document.querySelectorAll(".add-btn").forEach((button) => {
        button.addEventListener("click", () => {
            const card = button.closest(".product-card");
            const name = card.querySelector("b, strong").textContent;
            const price = card
                .querySelector(".product-price")
                .textContent.replace("₴", "")
                .trim();
            const img = card.querySelector(".product-img img");
            const image = img ? img.src : "images/no-image.png";
            addToCart(name, price, image);
        });
    });

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

            modalError.style.display = "none";
            // set title and choose which row to show
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
                // show password group
                modalRowSingle.classList.add("hidden");
                modalRowPassword.classList.remove("hidden");
                modalOld.value = "";
                modalNew.value = "";
                modalConfirm.value = "";
                modalOld.focus();
            } else {
                // show single input
                modalRowPassword.classList.add("hidden");
                modalRowSingle.classList.remove("hidden");
                const textNode = item.querySelector(".item-info div p");
                modalInput.type = "text";
                modalInput.value = textNode ? textNode.textContent.trim() : "";
                modalInput.focus();
            }
            openModal();
        });
    });

    function openModal() {
        if (!modal) return;
        modal.classList.remove("hidden");
        modal.setAttribute("aria-hidden", "false");
        // focus handled per-field above
    }
    function closeModal() {
        if (!modal) return;
        modal.classList.add("hidden");
        modal.setAttribute("aria-hidden", "true");
        currentTargetItem = null;
        currentField = null;
        modalError.style.display = "none";
    }

    modalSave.addEventListener("click", () => {
        if (!currentTargetItem || !currentField) return closeModal();
        const infoDiv = currentTargetItem.querySelector(".item-info div");
        if (currentField === "password") {
            const oldVal = modalOld.value.trim();
            const newVal = modalNew.value.trim();
            const conf = modalConfirm.value.trim();
            // basic validation
            if (!oldVal || !newVal || !conf) {
                modalError.textContent = "Всі поля пароля повинні бути заповнені.";
                modalError.style.display = "block";
                return;
            }
            if (newVal !== conf) {
                modalError.textContent =
                    "Новий пароль та підтвердження не співпадають.";
                modalError.style.display = "block";
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
            // (Optional) here you could send password change to server
            closeModal();
        } else {
            const newVal = modalInput.value.trim();
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

    [modalCancel, modalClose].forEach((el) => {
        if (!el) return;
        el.addEventListener("click", (e) => {
            e.preventDefault();
            closeModal();
        });
    });

    // close modal on overlay click (outside content)
    modal.addEventListener("click", (e) => {
        if (e.target === modal) closeModal();
    });

    // close on ESC
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closeModal();
    });
});

const searchInputShop = document.getElementById("search-input");
const allProducts = document.querySelectorAll(".product-card");
const catalog = document.querySelector(".catalog");

if (searchInputShop && allProducts.length > 0) {
    const noResults = document.createElement("p");
    noResults.textContent = "Товарів не знайдено 😕";
    noResults.style.textAlign = "center";
    noResults.style.color = "#555";
    noResults.style.display = "none";
    catalog.appendChild(noResults);

    searchInputShop.addEventListener("input", () => {
        const query = searchInputShop.value.toLowerCase().trim();
        let found = 0;

        allProducts.forEach((card) => {
            const title = card.querySelector("b, strong").textContent.toLowerCase();
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
            allProducts.forEach((card) => (card.style.display = "flex"));
            noResults.style.display = "none";
        }
    });
}