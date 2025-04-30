package org.pecasonline.common.httpclients

import org.pecasonline.common.httpclients.config.FeignClientProxyConfig
import org.pecasonline.common.httpclients.config.UserAgentConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "pecasOnlineClient",
    url = "http://www.pecas-on-line.com.br",
    configuration = [UserAgentConfig::class, FeignClientProxyConfig::class]
)
interface OldCompetitorClient {

    @GetMapping("/consultacod.php4")
    fun consultarPeca(
        @RequestParam("definirFabricante") definirFabricante: String = "todos",
        @RequestParam("Fabricante") fabricante: String = "AGCO",
        @RequestParam("PartNumber") partNumber: String,
        @RequestParam("Ordem") ordem: String = "Cidade",
        @RequestParam("Pesquisar") pesquisar: String = "Pesquisar"
    ): String
}
