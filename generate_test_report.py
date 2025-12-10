if ' | ' in full_name:
    parts = full_name.split(' | ')
    if len(parts) == 4:
        method_name_display = parts[0].strip() # Lấy tên hàm
        desc = parts[1].strip()                # Lấy mô tả
        data_input = parts[2].strip()          # Lấy input
        expected_result = parts[3].strip()     # Lấy expected