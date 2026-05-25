# Merchio - Mobile Merchandise E-Commerce App

## 📌 About Project

**Merchio** adalah aplikasi mobile e-commerce untuk merchandise official dan fanmade yang ditujukan untuk pecinta fandom, anime, game, music, pop culture, dan collectible.

Project ini dibuat menggunakan:

* Android Native (Java/Kotlin)
* SQLite untuk local database
* JSON/API untuk data katalog online
* RecyclerView & Fragment
* Glide untuk image loading
* Broadcast Receiver untuk monitoring internet

---

# 🎯 Product Goals

Membantu user:

* menemukan merchandise favorit
* menyimpan produk ke cart/wishlist
* melakukan checkout sederhana
* melacak riwayat pesanan
* menikmati UI modern dan mudah digunakan

---

# 👥 Target Users

* Gen Z
* Young people
* Anime & game fans
* Music fandom
* Pop culture lovers
* Collectors & fanmade merch buyers

---

# 🛠 Tech Stack

| Technology          | Usage                      |
| ------------------- | -------------------------- |
| Android Studio      | Development                |
| SQLite              | Local Database             |
| JSON API            | Online Product Data        |
| RecyclerView        | Product List/Grid          |
| Glide               | Load Image from URL        |
| Fragment            | Bottom Navigation          |
| Broadcast Receiver  | Internet Detection         |
| Intent & Parcelable | Send Data Between Activity |

---

# 📂 Data Architecture

## 1. JSON/API (Server Data)

Digunakan untuk data yang berasal dari server dan ditampilkan ke aplikasi Android.

### products

Digunakan pada:

* Home Screen
* Product Screen
* Detail Product

Field:

```json
{
  "id": "",
  "name": "",
  "category_id": "",
  "category_name": "",
  "brand": "",
  "type": "",
  "price": 0,
  "stock": 0,
  "sold_count": 0,
  "description": "",
  "image_url": "",
  "is_paidpromote": false
}
```

---

### categories

Digunakan untuk icon kategori di Home.

Contoh kategori:

* Clothing
* Photocard
* Keychain
* Figure
* Doll
* Standee
* Poster
* Badge
* Sticker

Field:

```json
{
  "id": "",
  "name": "",
  "icon_url": ""
}
```

---

### banners

Digunakan untuk banner promo di Home.

Field:

```json
{
  "id": "",
  "title": "",
  "subtitle": "",
  "image_url": "",
  "target_url": ""
}
```

---

# 💾 SQLite Database (Local Data)

SQLite digunakan untuk data yang dibuat oleh user di dalam aplikasi.

---

## users

Digunakan untuk:

* Register
* Login
* Profile

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
name TEXT
username TEXT
email TEXT
password TEXT
phone TEXT
avatar TEXT
header TEXT
created_at TEXT
```

---

## cart

Digunakan untuk:

* Cart Screen
* Checkout

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
user_id INTEGER
product_id TEXT
product_name TEXT
product_image TEXT
product_price INTEGER
type TEXT
quantity INTEGER
stock INTEGER
is_checked INTEGER
created_at TEXT
```

---

## orders

Digunakan untuk:

* Checkout
* Order Success
* Orders History

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
user_id INTEGER
order_code TEXT
total_price INTEGER
shipping_price INTEGER
tax INTEGER
payment_method TEXT
shipping_method TEXT
address TEXT
status TEXT
order_date TEXT
estimated_arrival TEXT
```

---

## order_items

Detail item dalam pesanan.

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
order_id INTEGER
product_id TEXT
product_name TEXT
product_image TEXT
price INTEGER
quantity INTEGER
type TEXT
```

---

## addresses

Digunakan untuk delivery address.

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
user_id INTEGER
recipient_name TEXT
phone TEXT
address TEXT
city TEXT
postal_code TEXT
is_default INTEGER
```

---

## recent_searches

Digunakan untuk fitur search history.

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
user_id INTEGER
keyword TEXT
created_at TEXT
```

---

## settings

Digunakan untuk:

* Dark mode
* Notification
* Theme preference

```sql
id INTEGER PRIMARY KEY AUTOINCREMENT
user_id INTEGER
dark_mode INTEGER
notification_enabled INTEGER
selected_theme TEXT
```

---

# 📱 Core Features

## 1. Splash Screen

Fitur:

* Menampilkan logo Merchio
* Delay 2–3 detik
* Redirect ke Login/Register/Home

Materi:

* Splash Screen
* Intent

---

## 2. Register

Fitur:

* Input email
* Username
* Password
* Confirm password
* Simpan user ke SQLite

Database:

* users

---

## 3. Login

Fitur:

* Login email/password
* Validasi input kosong
* Simpan status login

Database:

* users
* SharedPreferences / settings

---

## 4. Home

Fitur:

* Greeting user
* Search bar
* Banner paid promote
* Kategori produk
* Popular products
* Bottom navigation

API:

* products
* categories
* banners

Materi:

* Fragment
* RecyclerView
* JSON API

---

## 5. Product/Search

Fitur:

* Search product
* Recent search
* Product grid
* Favorite product
* Add to cart

API:

* products

SQLite:

* recent_searches
* cart

---

## 6. Detail Product

Fitur:

* Foto produk
* Harga
* Stock
* Variant selection
* Quantity selector
* Buy now
* Add to cart

API:

* products

SQLite:

* cart

Materi:

* Intent
* Parcelable / Serializable

---

## 7. Cart

Fitur:

* Item cart
* Checkbox item
* Select all
* Quantity adjustment
* Total calculation
* Checkout

SQLite:

* cart

CRUD:

* Create
* Read
* Update
* Delete

---

## 8. Checkout

Fitur:

* Pilih alamat
* Shipping method
* Payment method
* Order summary
* Buy button

SQLite:

* addresses
* orders
* order_items
* cart

---

## 9. Order Success

Fitur:

* Order confirmed
* Order ID
* Estimated arrival
* Purchased items
* Continue shopping

SQLite:

* orders
* order_items

---

## 10. Orders History

Fitur:

* Active orders
* Past orders
* Delivery status
* Reorder

SQLite:

* orders
* order_items

---

## 11. Profile

Fitur:

* Avatar
* Username
* Purchase history
* Settings
* Customer service

SQLite:

* users
* orders
* settings

---

# ✅ Mandatory Features

* Splash Screen
* Register & Login
* Home Fragment
* Product Fragment
* Cart Fragment
* Profile Fragment
* RecyclerView Grid
* CardView Product
* Detail Product Activity
* Intent Data Transfer
* Parcelable/Serializable Product
* SQLite CRUD Cart
* SQLite CRUD Wishlist
* SQLite Order History
* JSON/API Product Catalog
* Glide Image Loader
* Broadcast Receiver Internet Check
* Product Search
* Simple Checkout

---

# 🔄 Main Data Flow

```text
API products.json
        ↓
Home / Product Screen
        ↓
User Click Product
        ↓
Detail Product
        ↓
Add To Cart
        ↓
SQLite Cart
        ↓
Checkout
        ↓
SQLite Orders + Order Items
        ↓
Order Success / Order History
```

---

# 📦 Recommended Package Structure

```bash
com.merchio.app
├── activities
│   ├── SplashActivity
│   ├── LoginActivity
│   ├── RegisterActivity
│   ├── MainActivity
│   ├── DetailProductActivity
│   ├── CheckoutActivity
│   └── OrderSuccessActivity
│
├── fragments
│   ├── HomeFragment
│   ├── ProductFragment
│   ├── CartFragment
│   └── ProfileFragment
│
├── adapters
│   ├── ProductAdapter
│   ├── CartAdapter
│   ├── CategoryAdapter
│   └── OrderAdapter
│
├── models
│   ├── Product
│   ├── Category
│   ├── CartItem
│   ├── User
│   ├── Order
│   └── OrderItem
│
├── db
│   └── DbHelper
│
├── api
│   ├── ApiClient
│   └── ApiService
│
└── receiver
    └── NetworkReceiver
```

---

# 🚀 MVP (Minimum Viable Product)

Jika waktu pengerjaan terbatas, fokus ke fitur berikut:

* Splash Screen
* Register/Login
* Home dengan JSON API
* Product Grid
* Detail Product
* Add to Cart
* Cart CRUD SQLite
* Checkout
* Order Success
* Order History

Fitur di atas sudah mencakup:

* JSON Web Service
* SQLite CRUD
* RecyclerView
* Fragment
* Intent
* Parcelable
* Broadcast Receiver

---

# 📚 Learning Materials Covered

Project ini mencakup materi:

* SQLite CRUD
* RecyclerView
* Fragment Navigation
* Intent & Parcelable
* JSON Parsing
* API Integration
* Glide
* Broadcast Receiver
* SharedPreferences
* Local Database
* Android UI/UX

---

# 📌 Conclusion

Merchio dibuat sebagai project akhir Android yang menggabungkan:

* local database (SQLite)
* online JSON API
* modern UI mobile commerce
* CRUD operation
* RecyclerView & Fragment
* checkout flow sederhana

Sehingga project ini sudah sesuai untuk implementasi materi Android Intermediate hingga Final Project.
