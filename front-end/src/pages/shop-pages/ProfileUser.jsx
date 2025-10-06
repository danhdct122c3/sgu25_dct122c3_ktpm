import React, { useState, useEffect } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useNavigate } from "react-router-dom";
import api from "@/config/axios";
import { selectUser } from "@/store/auth";
import { useSelector } from "react-redux";
import LocationSelector from "@/components/shop/LocationSelector";
import { ToastContainer, toast } from "react-toastify";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import ChangePassword from "./ChangePassword";

export default function ProfileUser() {
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(false);

  const [email, setEmail] = useState(userData?.email || "");
  const [phone, setPhone] = useState(userData?.phone || "");
  const [location, setLocation] = useState("");
  const [street, setStreet] = useState("");
  const [address, setAddress] = useState("");
  const [fullName, setFullName] = useState(userData?.fullName || "");

  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const user = useSelector(selectUser);
  const userName = user ? user.sub : null;

  useEffect(() => {
    if (userData) {
      setEmail(userData.email || "");
      setPhone(userData.phone || "");
      setFullName(userData.fullName || "");
      setAddress(userData.address || "");
    }
  }, [userData]);

  useEffect(() => {
    console.log("=== PROFILE COMPONENT MOUNT ===");
    console.log("User from Redux:", user);
    console.log("Username extracted:", userName);
    console.log("Token exists:", !!token);
    console.log("===============================");
    
    if (!user) {
      console.log("❌ No user found, redirecting to login");
      navigate("/login");
      return;
    }
    
    if (!userName) {
      console.log("❌ No userName extracted from user");
      toast.error("Unable to get username from login data");
      return;
    }

    const fetchUserData = async () => {
      setLoading(true);
      try {
        console.log("=== FETCHING USER PROFILE ===");
        console.log("Username:", userName);
        console.log("API Endpoint:", `/users/profile?username=${userName}`);
        
        // Try multiple endpoints since /users/profile?username= returns 403 for ROLE_MEMBER
        let response = null;
        const profileEndpoints = [
          "/users/me",
          "/users/profile", 
          "/auth/me",
          `/users/profile?username=${userName}`
        ];
        
        for (const endpoint of profileEndpoints) {
          try {
            console.log(`Trying profile endpoint: ${endpoint}`);
            response = await api.get(endpoint);
            console.log(`✅ SUCCESS with ${endpoint}:`, response.data);
            break;
          } catch (err) {
            console.log(`❌ FAILED ${endpoint}:`, err.response?.status);
            if (endpoint === profileEndpoints[profileEndpoints.length - 1]) {
              throw err; // Last endpoint failed, throw error
            }
          }
        }
        
        const data = response.data.result;
        console.log("User Data:", data);
        
        setUserData(data);
        console.log("✅ User data loaded successfully");
        
      } catch (err) {
        console.error("=== FETCH USER PROFILE ERROR ===");
        console.error("Full error:", err);
        console.error("Response:", err.response?.data);
        console.error("Status:", err.response?.status);
        console.error("Username used:", userName);
        console.error("================================");
        
        // Show info instead of error - allow user to continue
        toast.info("Không thể tải thông tin cũ. Bạn có thể nhập thông tin mới và lưu.", {
          autoClose: 5000
        });
        
        // Set minimal user data to allow form to work
        setUserData({ 
          id: "unknown", 
          username: userName,
          email: "",
          phone: "",
          fullName: "",
          address: ""
        });
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [navigate, user, userName]);

  const handleLocationChange = (locationData) => {
    if (locationData && locationData.fullAddress) {
      setLocation(locationData.fullAddress);
      updateFullAddress(locationData.fullAddress, street);
    }
  };

  const updateFullAddress = (loc, str) => {
    const addressParts = [];
    if (loc) addressParts.push(loc);
    if (str) addressParts.push(str);
    const newAddress = addressParts.join(", ");
    setAddress(newAddress);

    if (userData) {
      setUserData((prev) => ({
        ...prev,
        address: newAddress,
      }));
    }
  };

  useEffect(() => {
    updateFullAddress(location, street);
  }, [location, street]);

  const handleUpdate = async (e) => {
    e.preventDefault();
    
    if (!userName) {
      alert("Cannot identify user. Please login again.");
      return;
    }
    
    // Chỉ cần ít nhất 1 field để update
    if (!email && !phone && !address && !fullName) {
      alert("Vui lòng điền ít nhất 1 thông tin để cập nhật");
      return;
    }

    const toastId = toast.loading("Updating user...");
    setLoading(true);

    let reversedAddress = address; // Initialize outside try block
    
    try {
      const addressPart = address.split(", ");
      reversedAddress = addressPart.reverse().join(", ");

      console.log("=== UPDATE USER REQUEST ===");
      console.log("Username:", userName);
      console.log("Email:", email);
      console.log("Phone:", phone);
      console.log("Address:", address);
      console.log("Reversed Address:", reversedAddress);
      console.log("Full Name:", fullName);
      console.log("UserData available:", !!userData);
      console.log("========================");

      // Chỉ gửi fields có data
      const payload = {};
      if (email) payload.email = email;
      if (phone) payload.phone = phone;  
      if (reversedAddress) payload.address = reversedAddress;
      if (fullName) payload.fullName = fullName;
      
      console.log("Payload to send:", payload);

      // Dùng endpoint PUT /users/{userId} với UUID
      if (!userData || !userData.id) {
        toast.update(toastId, {
          render: "❌ Không thể cập nhật: Thiếu thông tin user ID",
          type: "error",
          isLoading: false,
          autoClose: 5000,
        });
        return;
      }

      console.log(`🔄 Updating user via PUT /users/${userData.id}`);
      const response = await api.put(`/users/${userData.id}`, payload);
      console.log("✅ API Response status:", response.status);
      console.log("✅ API Response data:", response.data);
      
      // Success if status 200 or response has flag=true
      if (response.status === 200 || response?.data?.flag === true) {
        toast.update(toastId, {
          render: `✅ Cập nhật thông tin thành công!`,
          type: "success",
          isLoading: false,
          autoClose: 3000,
        });
        
        console.log("🎉 Profile update completed successfully!");
        
        // Refresh user data (wrap in try-catch to avoid breaking success flow)
        try {
          await fetchUser();
        } catch (fetchError) {
          console.warn("Failed to refresh user data:", fetchError);
          // Don't show error toast, update already succeeded
        }
        
      } else {
        toast.update(toastId, {
          render: response?.data?.message || "Cập nhật thất bại",
          type: "error",
          isLoading: false,
          autoClose: 5000,
        });
      }
    } catch (err) {
      console.error("=== UPDATE USER ERROR ===");
      console.error("Full error:", err);
      console.error("Response data:", err.response?.data);
      console.error("Status:", err.response?.status);
      console.error("Request payload:", {
        email: email,
        phone: phone,
        address: reversedAddress,
        fullName: fullName,
      });
      console.error("User ID:", userData?.id);
      console.error("========================");
      
      let errorMessage = "Failed to update user";
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err.response?.status === 404) {
        errorMessage = "User not found";
      } else if (err.response?.status === 401) {
        errorMessage = "Unauthorized - please login again";
      } else if (err.response?.status === 400) {
        errorMessage = "Invalid data provided";
      }
      
      toast.update(toastId, {
        render: errorMessage,
        type: "error",
        isLoading: false,
        autoClose: 5000,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <ToastContainer
        position="top-right"
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition:Bounce
      />
      <div className="container mx-auto">
        <div className="col-span-1 border-r flex justify-center">
          <Tabs defaultValue="profile" className="w-full">
            <TabsList className='bg-slate-100'>
              <TabsTrigger value="profile">Hồ Sơ Của Bạn</TabsTrigger>
              <TabsTrigger value="change-password">Thay đổi mật khẩu</TabsTrigger>
            </TabsList>
            <TabsContent value="profile">
              <div className="col-span-2 p-4">
                <div className="mt-1">
                  <Card className="w-full border-0">
                    <CardHeader>
                      <CardDescription className="font-bold text-center">
                        Hiện và chỉnh sửa thông tin hồ sơ của bạn
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="grid w-full gap-6 border rounded-sm p-4 mb-4">
                        <div className="grid gap-2">
                          <Label>Họ và tên</Label>
                          <Input
                            id="fullName"
                            type="text"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                            className="border rounded-md p-2 w-full"
                          />
                        </div>

                      </div>

                      <div className="grid w-full gap-6 border rounded-sm p-4">
                        <div className="grid grid-cols-2 gap-4">
                          <div className="grid gap-2">
                            <Label>Email</Label>
                            <Input
                              id="email"
                              type="email"
                              value={email}
                              onChange={(e) => setEmail(e.target.value)}
                              className="border rounded-md p-2 w-full"
                            />
                          </div>

                          <div className="grid gap-2">
                            <Label>Số điện thoại</Label>
                            <Input
                              id="phone"
                              type="text"
                              value={phone}
                              onChange={(e) => setPhone(e.target.value)}
                              className="border rounded-md p-2 w-full"
                            />
                          </div>
                        </div>
                        <div className="grid gap-2">
                          <Label>Địa chỉ hiện tại</Label>
                          <Input
                            id="address"
                            type="text"
                            value={address}
                            className="border rounded-md p-2 w-full"
                            readOnly
                          />
                        </div>
                        <div className="grid gap-2">
                          <Label>Đường</Label>
                          <Input
                            id="street"
                            type="text"
                            value={street}
                            onChange={(e) => setStreet(e.target.value)}
                            className="border rounded-md p-2 w-full"
                          />
                          <LocationSelector
                            onLocationChange={handleLocationChange}
                          />
                        </div>
                      </div>

                      <CardFooter className="flex justify-between">
                        <div className="space-x-2">
                          <Button
                            type="button"
                            variant="outline"
                            onClick={async () => {
                              try {
                                console.log("=== TESTING API ENDPOINTS ===");
                                const token = localStorage.getItem("token");
                                console.log("Token exists:", !!token);
                                
                                // Test different endpoints
                                const testEndpoints = [
                                  "/users/me", // Most common for current user
                                  "/users/profile", // Without username param
                                  "/auth/me", // Alternative auth endpoint
                                  "/user/current", // Another common pattern
                                  `/users/profile?username=${userName}`, // Original (likely will fail)
                                  "/users/current",
                                  "/profile/me",
                                  "/api/me"
                                ];
                                
                                for (const endpoint of testEndpoints) {
                                  try {
                                    console.log(`Testing GET ${endpoint}...`);
                                    const response = await api.get(endpoint);
                                    console.log(`✅ SUCCESS ${endpoint}:`, response.data);
                                    toast.success(`API works: ${endpoint}`);
                                    break;
                                  } catch (err) {
                                    console.log(`❌ FAILED ${endpoint}:`, err.response?.status, err.response?.data);
                                  }
                                }
                              } catch (err) {
                                console.error("Test failed:", err);
                              }
                            }}
                          >
                            🧪 Test API
                          </Button>
                          
                          <Button
                            type="button"
                            variant="outline"
                            onClick={() => {
                              const token = localStorage.getItem("token");
                              console.log("=== TOKEN DEBUG ===");
                              console.log("Token exists:", !!token);
                              console.log("Token length:", token?.length);
                              console.log("Full token:", token); // COPY THIS FOR TESTING
                              
                              if (token) {
                                try {
                                  const parts = token.split('.');
                                  const payload = JSON.parse(atob(parts[1]));
                                  console.log("Token payload:", payload);
                                  console.log("Token expires:", new Date(payload.exp * 1000));
                                  console.log("Token expired?", payload.exp * 1000 < Date.now());
                                  console.log("Username from token:", payload.sub);
                                } catch (e) {
                                  console.error("Token parse error:", e);
                                }
                              }
                              console.log("==================");
                            }}
                          >
                            🔍 Check Token
                          </Button>
                        </div>
                        
                        <Button
                          type="submit"
                          className="bg-blue-500"
                          onClick={handleUpdate}
                          disabled={loading}
                        >
                          Lưu thay đổi
                        </Button>
                      </CardFooter>
                    </CardContent>
                  </Card>
                </div>
              </div>
            </TabsContent>
            <TabsContent value="change-password">
              <ChangePassword/>
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  );
}
