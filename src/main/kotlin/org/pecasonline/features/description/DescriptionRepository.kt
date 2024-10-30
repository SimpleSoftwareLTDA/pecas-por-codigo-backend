package org.pecasonline.features.description

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DescriptionRepository : JpaRepository<Description, Int>