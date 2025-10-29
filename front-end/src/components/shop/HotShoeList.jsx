import React from "react";
import { FaFire } from "react-icons/fa";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "../ui/button";
import { BiSolidDetail } from "react-icons/bi";
import { formatterToVND } from "@/utils/formatter";
import { Link } from "react-router-dom";
import { getImageUrl } from "@/utils/imageHelper";

export default function HotShoeList({ shoes = [], title, titlte }) {
  // Chấp nhận cả 'title' và 'titlte'
  const heading = title ?? titlte ?? "Sản phẩm hot";

  return (
    <div className="bg-white rounded-sm p-4 my-4">
      {/* Header */}
      <div className="flex items-center">
        <p className="text-3xl font-bold me-2">{heading}</p>
        <FaFire className="w-8 h-8 text-red-500" />
      </div>

      {/* ✅ Grid: items-stretch để card cao đều nhau */}
      <div className="grid grid-cols-2 md:grid-cols-2 lg:grid-cols-4 gap-5 mt-4 items-stretch">
        {shoes.map((shoe) => (
          <Card
            key={shoe.id}
            className="h-full flex flex-col overflow-hidden hover:border-stone-950 cursor-pointer"
          >
            <CardHeader className="pb-0">
              {/* ✅ Tiêu đề 2 dòng + giữ chiều cao tối thiểu */}
              <CardTitle
                className="capitalize line-clamp-2 min-h-[48px]"
                title={shoe.name}
              >
                {shoe.name}
              </CardTitle>
            </CardHeader>

            <CardContent className="flex-1 flex flex-col">
              {/* ✅ Khung ảnh cố định tỉ lệ, ảnh nhỏ căn giữa (object-contain) */}
              <div className="w-full aspect-square grid place-items-center bg-white overflow-hidden">
                <img
                  src={getImageUrl(shoe.images?.[0]?.url)}
                  alt={shoe.name}
                  className="h-full w-full object-contain p-2"
                  onError={(e) => {
                    e.currentTarget.src =
                      "https://via.placeholder.com/300?text=IMG";
                  }}
                />
              </div>

              {/* Giá */}
              <div className="mt-3 flex justify-between items-start">
                <p className="text-xl font-bold">
                  {formatterToVND.format(shoe.price)}
                </p>
                {shoe.fakePrice && (
                  <p className="text-xl font-bold line-through">
                    {formatterToVND.format(shoe.fakePrice)}
                  </p>
                )}
              </div>

              {/* ✅ Đẩy nút xuống đáy → mọi card cao bằng nhau */}
              <div className="mt-auto" />
            </CardContent>

            {/* ✅ Nút full-width giống trang sản phẩm */}
            <CardFooter className="flex justify-center pt-0">
              <Link to={`/shoes/${shoe.id}`} className="w-full">
                <Button className="w-full cursor-pointer hover:bg-slate-500 hover:text-slate-950 flex items-center justify-center">
                  <BiSolidDetail className="w-6 h-6 mr-2" />
                  Xem chi tiết
                </Button>
              </Link>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
}