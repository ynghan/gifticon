export type Coordinates = {
  lat: number;
  lng: number;
};

export type Marker = {
  id: string;
  position: Coordinates;
  place_name: string;
  address_name: string;
  place_url: string;
  category_group_name: string;
};
