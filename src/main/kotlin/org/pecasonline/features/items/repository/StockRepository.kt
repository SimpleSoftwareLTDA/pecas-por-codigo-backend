package org.filldb.novopecasonline.features.repository

import org.pecasonline.features.items.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, Int>