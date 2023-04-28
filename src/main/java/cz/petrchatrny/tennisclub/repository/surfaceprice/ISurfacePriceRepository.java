package cz.petrchatrny.tennisclub.repository.surfaceprice;

import cz.petrchatrny.tennisclub.model.SurfacePrice;

public interface ISurfacePriceRepository {
    SurfacePrice create(SurfacePrice price);

    void invalidate(Long id);
}