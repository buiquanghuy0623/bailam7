<!-- Khu vực Tìm kiếm và Sắp xếp -->
    <div style="width: 80%; margin: 0 auto 15px auto; display: flex; justify-content: space-between; align-items: center;">
        <!-- Form tìm kiếm theo Country -->
        <form action="${pageContext.request.contextPath}/users" method="get" style="display: flex; gap: 8px;">
            <input type="hidden" name="action" value="search" />
            <input type="text" name="country" placeholder="Nhập quốc gia cần tìm..." value="${requestScope.searchCountry}" style="padding: 8px; width: 220px; border: 1px solid #ccc; border-radius: 4px;" />
            <button type="submit" class="btn" style="background-color: #2980b9; border: none; cursor: pointer; color: white; padding: 8px 12px; border-radius: 4px; font-weight: bold;">Tìm kiếm</button>
            <a href="${pageContext.request.contextPath}/users" class="btn" style="background-color: #95a5a6; padding: 8px 12px; text-decoration: none; border-radius: 4px; color: white; font-weight: bold;">Làm mới</a>
        </form>

        <!-- Nút sắp xếp theo tên -->
        <div>
            <a href="${pageContext.request.contextPath}/users?action=sort" class="btn" style="background-color: #8e44ad; padding: 9px 12px; text-decoration: none; border-radius: 4px; color: white; font-weight: bold;">Sắp xếp theo Tên (A-Z)</a>
        </div>
    </div>
