# 🐛 DEBUG - Tại sao vẫn vào được Admin?

## ❌ Triệu chứng từ ảnh chụp màn hình:
- Hiển thị "Xin chào: ()" - có nghĩa là `user` là `null` hoặc `undefined`
- Nhưng vẫn vào được trang `/admin`

## 🔍 Nguyên nhân có thể:

### 1. **Frontend chưa được rebuild/reload** ⚠️
Code đã sửa nhưng browser vẫn chạy code cũ trong cache.

**Giải pháp**:
```bash
# Bước 1: Stop dev server (Ctrl+C trong terminal đang chạy npm)

# Bước 2: Clear cache và restart
cd front-end
rm -rf node_modules/.vite  # Clear Vite cache
npm run dev

# Bước 3: Trong browser
# - Clear cache: Ctrl+Shift+Delete → Clear All
# - Hard reload: Ctrl+Shift+R
# - Hoặc dùng Incognito: Ctrl+Shift+N
```

### 2. **Token trong localStorage nhưng không hợp lệ** 🔑

**Kiểm tra**:
1. Mở DevTools (F12)
2. Vào tab **Application** → **Local Storage** → `http://localhost:3000`
3. Tìm key `token`
4. Copy giá trị token
5. Vào https://jwt.io và paste token vào
6. Kiểm tra:
   - ✅ Token có decode được không?
   - ✅ Field `scope` có format đúng không? (VD: `ROLE_ADMIN` hoặc `ROLE_USER`)
   - ✅ Field `exp` còn hạn không? (so với thời gian hiện tại)
   - ✅ Field `sub` có username không?

**Nếu token không hợp lệ**:
```javascript
// Trong Console (F12), chạy:
localStorage.clear();
location.reload();
// Sau đó đăng nhập lại
```

### 3. **ProtectedRoute không được áp dụng** 🚧

**Kiểm tra**: Mở Console (F12) và tìm các log sau khi vào `/admin`:
```
Nếu thấy các log này:
🔒 ProtectedRoute - User: ...
🔒 ProtectedRoute - Token exists: ...
🔒 ProtectedRoute - Required Role: ADMIN
```
→ ProtectedRoute ĐANG hoạt động

```
Nếu KHÔNG thấy log nào:
```
→ ProtectedRoute KHÔNG được gọi (có thể route config sai)

### 4. **Kiểm tra route config trong App.jsx** 📁

Mở file `src/App.jsx` và tìm phần admin route:

```jsx
// Phải có cấu trúc này:
{
  path: "/admin",
  element: (
    <ProtectedRoute requiredRole="ADMIN">  {/* ← Phải có dòng này */}
      <RootLayoutAdmin />
    </ProtectedRoute>
  ),
  children: [...]
}
```

**KHÔNG được là**:
```jsx
{
  path: "/admin",
  element: <RootLayoutAdmin />,  {/* ← SAI! Thiếu ProtectedRoute */}
  children: [...]
}
```

---

## ✅ HƯỚNG DẪN DEBUG TỪNG BƯỚC:

### Bước 1: Clear Everything
```bash
# Trong terminal frontend:
# 1. Stop server (Ctrl+C nếu đang chạy)

# 2. Clear Vite cache
cd d:\HKI_4\KiemThuPhamMem\project\capstone\front-end
Remove-Item -Recurse -Force node_modules\.vite -ErrorAction SilentlyContinue

# 3. Restart
npm run dev
```

### Bước 2: Clear Browser
```
1. Mở Chrome/Edge
2. Nhấn Ctrl+Shift+Delete
3. Chọn:
   ✅ Cached images and files
   ✅ Cookies and other site data
4. Nhấn "Clear data"
5. Đóng tất cả tab
```

### Bước 3: Test Lại
```
1. Mở browser MỚI (hoặc Incognito: Ctrl+Shift+N)
2. Vào http://localhost:3000 (hoặc port của bạn)
3. Mở DevTools (F12) → Tab Console
4. Thử truy cập http://localhost:3000/admin
```

### Bước 4: Xem Console Log

**Nếu thấy**:
```
❌ No token or user found - redirecting to login
```
→ ✅ ĐÚNG! Đã bị chặn

**Nếu thấy**:
```
✅ Access granted - rendering protected content
```
→ Có token hợp lệ với role ADMIN (bình thường nếu bạn đã login)

**Nếu KHÔNG thấy log nào**:
→ ProtectedRoute không chạy → Kiểm tra App.jsx

### Bước 5: Test với các tài khoản khác nhau

#### Test A: Không đăng nhập
```
1. Clear localStorage: localStorage.clear()
2. Reload: location.reload()
3. Vào: /admin
4. Kết quả mong đợi: Redirect về /login
```

#### Test B: Đăng nhập USER
```
1. Login với: user / user123
2. Vào: /admin
3. Kết quả mong đợi: Hiển thị "🚫 Không có quyền truy cập"
```

#### Test C: Đăng nhập ADMIN
```
1. Login với: admin / admin123
2. Vào: /admin
3. Kết quả mong đợi: Vào được, hiển thị dashboard
```

---

## 🔧 QUICK FIX - Chạy commands này:

Mở terminal trong VS Code và chạy:

```powershell
# 1. Stop frontend nếu đang chạy (Ctrl+C)

# 2. Clear cache và restart
cd d:\HKI_4\KiemThuPhamMem\project\capstone\front-end
if (Test-Path "node_modules\.vite") { Remove-Item -Recurse -Force node_modules\.vite }
npm run dev
```

Sau đó trong browser:
```javascript
// Mở Console (F12) và chạy:
localStorage.clear();
sessionStorage.clear();
location.reload();
```

---

## 📸 Screenshot để debug

Nếu vẫn không được, hãy chụp màn hình:

1. **Console tab** (F12 → Console) - xem có log gì
2. **Network tab** (F12 → Network) - xem API login response
3. **Application tab** (F12 → Application → Local Storage) - xem token
4. **Paste token vào jwt.io** - xem decode

---

## ⚠️ LƯU Ý QUAN TRỌNG:

### Vite Dev Server Cache
Vite có cache rất mạnh. Nếu sửa code mà không thấy thay đổi:
```bash
# Phải clear cache Vite:
rm -rf node_modules/.vite
# Hoặc trên Windows PowerShell:
Remove-Item -Recurse -Force node_modules\.vite
```

### Browser Cache
Browser cũng cache code. Luôn dùng:
- **Hard Reload**: Ctrl+Shift+R
- **Incognito Mode**: Ctrl+Shift+N (để test không có cache)

### Hot Module Replacement (HMR)
Đôi khi HMR của Vite không reload đúng. Nếu thấy lạ:
- Restart dev server
- Hard reload browser

---

## 🎯 CHECKLIST CUỐI CÙNG:

Sau khi làm các bước trên, kiểm tra:

- [ ] Frontend dev server đã restart ✅
- [ ] Browser cache đã clear ✅
- [ ] LocalStorage đã clear ✅
- [ ] Mở Console thấy logs từ ProtectedRoute ✅
- [ ] Test không login → redirect về /login ✅
- [ ] Test USER login → hiển thị "Không có quyền" ✅
- [ ] Test ADMIN login → vào được admin ✅

Nếu tất cả đều ✅ → Vấn đề đã fix!

Nếu vẫn có vấn đề → Hãy gửi:
1. Screenshot Console tab
2. Screenshot Application → Local Storage
3. Token decode từ jwt.io
