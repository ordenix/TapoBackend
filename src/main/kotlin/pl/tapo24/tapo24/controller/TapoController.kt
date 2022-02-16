package pl.tapo24.tapo24.controller


import org.springframework.web.bind.annotation.*
import pl.tapo24.tapo24.dao.FavoritesOffensesRepository
import pl.tapo24.tapo24.dao.Repository.ModuleClickedRepository
import pl.tapo24.tapo24.dao.UniqueInstallationIdRepository
import pl.tapo24.tapo24.dao.entity.FavoritesOffenses
import pl.tapo24.tapo24.dao.entity.ModuleClicked
import pl.tapo24.tapo24.dao.entity.UniqueInstallationId
import pl.tapo24.tapo24.others.gen_UID
import java.util.*
import javax.validation.Valid





@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api")
class TapoController(private val UniqueInstallationIdRepository: UniqueInstallationIdRepository) {

    @GetMapping("/get_UID")
    fun getUidInstallation(): Any {
        do {
            val UID = gen_UID()
            if (UniqueInstallationIdRepository.findUID(UID)?.UID.isNullOrEmpty()) {
                UniqueInstallationIdRepository.save(UniqueInstallationId(0, UID))
                return Collections.singletonMap("UID", UID)
            }
        } while (true)
    }



}
@RestController
class TapoFavorite(private val FavoritesOffensesRepository: FavoritesOffensesRepository){
    @PostMapping("/put_favorite")
    fun putFavorite(@Valid @RequestBody data: FavoritesOffenses){
        var repo = FavoritesOffensesRepository.findUID(data.UID)
        if (repo?.UID.isNullOrEmpty()) {
           FavoritesOffensesRepository.save(data)
       } else {
           repo!!.favorite = data.favorite
            FavoritesOffensesRepository.save(repo)
        }
        return
    }
}
@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/api")
class TapoModuleClicked(private  val  ModuleClicked: ModuleClickedRepository) {
    //@CrossOrigin(origins = ["http://localhost:8082"])
    @PostMapping("/module_clicked")
    fun putModuleCLicked(@Valid @RequestBody data: ModuleClicked){
        data.moduleName = data.moduleName.lowercase()
        if (ModuleClicked.existsByModuleName(moduleName = data.moduleName)) {
            val response = ModuleClicked.findByModuleNameIs(moduleName = data.moduleName)
            val times = response.times +1
            ModuleClicked.updateTimesByModuleNameIs(times, moduleName = data.moduleName)
        } else  {
            ModuleClicked.save(data)
        }
        return
    }
}
