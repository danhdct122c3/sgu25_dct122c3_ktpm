import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { fetchFilterOptions } from "@/store/filter";
import { useEffect } from "react";
import { useFormField } from "../ui/form";


export default function ListBrand() {
  const dispatch = useDispatch();
  const { brands, loading } = useSelector((state) => state.filter);

  useEffect(() => {
    dispatch(fetchFilterOptions());
  }, [dispatch]);

  // Map brand names to local logo files
  const getBrandLogo = (brandName) => {
    const normalizedName = brandName.toLowerCase().replace(/\s+/g, '');
    const logoMap = {
      'nike': '/brand-images/nike-logo.png',
      'adidas': '/brand-images/adidas-logo.png',
      'puma': '/brand-images/puma-logo.png',
      'reebok': '/brand-images/reebok-logo.png',
      'converse': '/brand-images/converse-logo.png',
      'vans': '/brand-images/vans-logo.jpg',
      'fila': '/brand-images/fila-logo.webp',
      'skechers': '/brand-images/CORP_SKX_BLK-logo.jpg',
      'underarmour': '/brand-images/underarmour-logo.png',
      'newbalance': '/brand-images/newbalance-logo.jpg'
    };
    return logoMap[normalizedName] || null;
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div className="bg-white my-4 py-4 rounded-sm">
      <div className="text-3xl text-center font-semibold underline underline-offset-8 mb-16">
        Chọn hãng giày
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 px-4">
        {brands.map((item, index) => {
          const localLogo = getBrandLogo(item.brandName);
          return (
            <Link key={index} to={`/shoes?brand=${item.brandId}`}>
              <div className="flex flex-col items-center cursor-pointer p-4 border-2 border-gray-200 rounded-lg hover:border-gray-400 hover:shadow-lg transition-all duration-300 bg-white">
                {localLogo ? (
                  <img 
                    src={localLogo} 
                    alt={item.brandName} 
                    className="w-20 h-20 object-contain mb-3" 
                  />
                ) : (
                  <div className="w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center mb-3">
                    <span className="text-2xl font-bold text-gray-600">
                      {item.brandName.charAt(0)}
                    </span>
                  </div>
                )}
                <div className="font-bold text-xl">{item.brandName}</div>
              </div>
            </Link>
          );
        })}
      </div>
    </div>
  );
}
