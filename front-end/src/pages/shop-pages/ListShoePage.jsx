import { useCallback } from "react";
import api from "@/config/axios";
import { useState, useEffect } from "react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
// removed unused IoIosSearch import

import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
// removed unused Select imports
import { Button } from "@/components/ui/button";
import { BiSolidDetail } from "react-icons/bi";
// removed unused FiShoppingBag import
import { Input } from "@/components/ui/input";
import { getImageUrl } from "@/utils/imageHelper";
// removed unused ComboBoxOrderBy import
import { Link, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchFilterOptions } from "@/store/filter";
import { useShopFilters } from "@/hooks/useShopFilters";
// removed unused cartActions and selectUser imports
import { ToastContainer, Bounce } from "react-toastify";
import { formatterToVND } from "../../utils/formatter";
import { ChevronLeft, ChevronRight } from "lucide-react";
// removed unused useNavigate import

import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";

import { FaHome } from "react-icons/fa";

export default function ListShoePage() {
  const [shoes, setShoes] = useState([]);
  // removed unused loading state
  const [prevFilters, setPrevFilters] = useState({});
  const [initialLoad, setInitialLoad] = useState(true);

  const [currentPage, setCurrentPage] = useState(1);
  const [searchQuery, setSearchQuery] = useState("");
  const sortOrder = "asc"; // fixed: only using a constant sort order
  const { id } = useParams();

  const itemsPerPage = 8;

  const filteredShoe = shoes.filter((shoe) => {
    return shoe.name.toLowerCase().includes(searchQuery.toLowerCase());
  });

  const sortedShoes = [...filteredShoe].sort(
    (a, b) => (sortOrder === "asc" ? a.price - b.price : b.price - a.price)
  );

  const indexOfLastShoe = currentPage * itemsPerPage;
  const indexOfFirstShoe = indexOfLastShoe - itemsPerPage;
  const currentShoes = sortedShoes.slice(indexOfFirstShoe, indexOfLastShoe);

  const totalPages = Math.ceil(sortedShoes.length / itemsPerPage);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  // removed unused handleAddToCart

  const dispatch = useDispatch();
  const { brands, categories, genders } = useSelector((state) => state.filter);

  const { filters } = useShopFilters();

  useEffect(() => {
    if (!brands.length || !categories.length || !genders.length) {
      dispatch(fetchFilterOptions());
    }
  }, [dispatch, brands.length, categories.length, genders.length]);

  const fetchShoes = useCallback(async () => {
    try {
      let endpoint = "/shoes";
      if (filters.category) {
        endpoint = "/shoes/by-category";
      } else if (filters.brand) {
        endpoint = "/shoes/by-brand";
      } else if (filters.gender) {
        endpoint = "/shoes/by-gender";
      }

      const { data } = await api.get(endpoint, { params: filters });
      setShoes(data.result);
      setPrevFilters(filters);
    } catch (error) {
      console.error("Error fetching shoes:", error);
    }
  }, [filters]);

  useEffect(() => {
    if (initialLoad) {
      setInitialLoad(false);
      fetchShoes();
      return;
    }

    if (JSON.stringify(prevFilters) !== JSON.stringify(filters)) {
      fetchShoes();
    }
  }, [filters, initialLoad, prevFilters, fetchShoes]);

  return (
    
<main className="container mx-auto bg-white rounded-sm">
    {/* ✅ ToastContainer: đúng cú pháp transition */}
    <ToastContainer
      position="bottom-right"
      hideProgressBar={false}
      newestOnTop={false}
      closeOnClick
      rtl={false}
      pauseOnFocusLoss
      draggable
      pauseOnHover
      theme="light"
      transition={Bounce}
    />

    {/* ✅ Breadcrumb theo route */}
    <div className="px-4 py-2">
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink asChild>
              <Link to="/" className="inline-flex items-center gap-2">
                <FaHome className="h-4 w-4" aria-hidden="true" />
                <span>Trang chủ</span>
              </Link>
            </BreadcrumbLink>
          </BreadcrumbItem>

          <BreadcrumbSeparator />

          {id ? (
            <>
              <BreadcrumbItem>
                <BreadcrumbLink asChild>
                  <Link to="/shoes">Sản phẩm</Link>
                </BreadcrumbLink>
              </BreadcrumbItem>

              <BreadcrumbSeparator />

              <BreadcrumbItem>
                <BreadcrumbPage>Chi tiết</BreadcrumbPage>
              </BreadcrumbItem>
            </>
          ) : (
            <BreadcrumbItem>
              <BreadcrumbPage>Sản phẩm</BreadcrumbPage>
            </BreadcrumbItem>
          )}
        </BreadcrumbList>
      </Breadcrumb>
    </div>
      <div className="flex p-4">
        <div className="w-1/3 me-4">
          <Accordion type="single" collapsible>
            <AccordionItem value="item-1">
              <AccordionTrigger>Hãng giày</AccordionTrigger>
              {brands.map((brand, index) => (
                <AccordionContent key={index}>
                  <Link
                    to={`/shoes?brand=${brand.brandId}`}
                    className="hover:underline"
                  >
                    {brand.brandName}
                  </Link>
                </AccordionContent>
              ))}
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger>Loại giày</AccordionTrigger>
              {categories.map((category, index) => (
                <AccordionContent key={index}>
                  <Link
                    to={`/shoes?category=${category.value}`}
                    className="hover:underline"
                  >
                    {category.name}
                  </Link>
                </AccordionContent>
              ))}
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger>Giới tính</AccordionTrigger>
              {genders.map((gender, index) => (
                <AccordionContent key={index}>
                  <Link
                    to={`/shoes?gender=${gender.value}`}
                    className="hover:underline"
                  >
                    {gender.name}
                  </Link>
                </AccordionContent>
              ))}
            </AccordionItem>
          </Accordion>
        </div>
        <div className="flex-col">
          <div className="mb-8 mt-4 flex justify-between">
            <div className="relative w-1/3 flex">
              <Input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="max-w-sm"
                placeholder="Tìm kiếm..."
              />
            </div>
            {/* <div>
              <Select value={sortOrder} onValueChange={setSortOrder}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Sort by price" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="asc">Price: Low to High</SelectItem>
                  <SelectItem value="desc">Price: High to Low</SelectItem>
                </SelectContent>
              </Select>
            </div> */}
          </div>
          
          <div className="grid grid-cols-2 md:grid-cols-2 lg:grid-cols-4 gap-4 items-stretch">
            {currentShoes.map((shoe) => (
              <Card
                key={shoe.id}
                className="h-full flex flex-col overflow-hidden hover:border-stone-950 cursor-pointer"
              >
                <CardHeader className="pb-0">
                  <CardTitle className="capitalize line-clamp-2 min-h-[48px]" title={shoe.name}>
                    {shoe.name}
                  </CardTitle>
                </CardHeader>

                <CardContent className="flex-1 flex flex-col">
                  <div className="w-full aspect-square grid place-items-center bg-white overflow-hidden">
                    <img
                      src={getImageUrl(shoe.images?.[0]?.url)}
                      alt={shoe.name}
                      className="h-full w-full object-contain p-2"
                      onError={(e) => {
                        e.currentTarget.src = "https://via.placeholder.com/300?text=IMG";
                      }}
                    />
                  </div>

                  <div className="mt-3 flex justify-between items-start">
                    <p className="text-xl font-bold">
                      {formatterToVND.format(shoe.price)}
                    </p>
                    <p className="text-xl font-bold line-through">
                      {formatterToVND.format(shoe.fakePrice)}
                    </p>
                  </div>

                  <div className="mt-auto" />
                </CardContent>

                <CardFooter className="flex justify-center pt-0">
                  <Link to={`/shoes/${shoe.id}`} className="w-full">
                    <Button className="w-full cursor-pointer hover:bg-slate-500 hover:text-slate-950">
                      <BiSolidDetail className="w-6 h-6 mr-2" />
                      Xem chi tiết
                    </Button>
                  </Link>
                </CardFooter>
              </Card>
            ))}
          </div>
          <div className="flex justify-between items-center my-4">
            <div>
              Hiện trang {indexOfFirstShoe + 1} trên{" "}
              {Math.min(indexOfLastShoe, sortedShoes.length)} từ{" "}
              {sortedShoes.length} giày
            </div>
            <div className="flex spacex-x-2">
              <Button
                variant="outline"
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
              >
                <ChevronLeft className="h-4 w-4" />
              </Button>
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(
                (page) => (
                  <Button
                    key={page}
                    variant={currentPage === page ? "default" : "outline"}
                    onClick={() => handlePageChange(page)}
                  >
                    {page}
                  </Button>
                )
              )}
              <Button
                variant="outline"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
              >
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
