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

export default function HotShoeList({ shoes, titlte }) {
  return (
    <div className="bg-white rounded-sm p-4 my-4">
      <div className="flex">
        <p className="text-3xl font-bold me-2 items-center">{titlte}</p>
        <FaFire className="w-8 h-8 text-red-500" />
      </div>

      {/* Grid có thể giữ như cũ; nếu muốn giống trang sản phẩm đổi gap-4 */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-5 mt-4">
        {shoes.map((shoe) => (
          <Card key={shoe.id} className="hover:border-stone-950 cursor-pointer">
            <CardHeader>
              <CardTitle className="capitalize">{shoe.name}</CardTitle>
            </CardHeader>

            <CardContent>
              <img src={getImageUrl(shoe.images?.[0]?.url)} alt={shoe.name} />
              <div className="flex justify-between">
                <p className="text-xl font-bold mt-2">
                  {formatterToVND.format(shoe.price)}
                </p>
                {shoe.fakePrice && (
                  <p className="text-xl font-bold mt-2 line-through">
                    {formatterToVND.format(shoe.fakePrice)}
                  </p>
                )}
              </div>
            </CardContent>

            {/* ✅ Làm nút giống trang sản phẩm */}
            <CardFooter className="flex justify-center">
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