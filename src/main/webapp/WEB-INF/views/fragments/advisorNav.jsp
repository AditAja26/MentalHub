<nav style="display: flex; justify-content: space-between; align-items: center; padding: 15px 60px; background: white; box-shadow: 0 2px 10px rgba(0,0,0,0.05); font-family: 'Segoe UI', sans-serif;">
    <div style="display: flex; align-items: center; gap: 10px;">
        <img src="/resources/images/logo.png" width="40" alt="Logo">
        <span style="font-weight: bold; color: #4A90E2; font-size: 22px;">MentalHub+</span>
    </div>
    
    <div style="display: flex; gap: 40px; font-weight: 500;">
        <a href="${pageContext.request.contextPath}/advisor/home" style="text-decoration: none; color: #555;">Home</a>
        <a href="${pageContext.request.contextPath}/advisor/report" style="text-decoration: none; color: #555;">Generate Report</a>
        <a href="${pageContext.request.contextPath}/advisor/monitor" style="text-decoration: none; color: #555;">Monitor Dashboard</a>
    </div>

    <div style="display: flex; align-items: center; gap: 20px;">
        <a href="#" style="text-decoration: none; color: #555;">Logout</a>
        <span style="font-size: 20px; position: relative;">🔔<span style="position: absolute; top: -5px; right: -5px; background: red; color: white; border-radius: 50%; padding: 2px 5px; font-size: 10px;">1</span></span>
        <div style="width: 35px; height: 35px; background: #E0E0E0; border-radius: 50%;"></div>
    </div>
</nav>