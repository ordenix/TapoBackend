package pl.tapo24.tapo24.controller


import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.tapo24.tapo24.dao.FavoritesOffensesRepository
import pl.tapo24.tapo24.dao.Repository.*
import pl.tapo24.tapo24.dao.UniqueInstallationIdRepository
import pl.tapo24.tapo24.dao.entity.*
import pl.tapo24.tapo24.others.AgentsStatusInstallation
import pl.tapo24.tapo24.others.Uid
import pl.tapo24.tapo24.others.VersionStatusInstallation
import pl.tapo24.tapo24.others.gen_UID
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.util.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"])
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/installation")
class TapoController(private val UniqueInstallationIdRepository: UniqueInstallationIdRepository,
                     private val VersionsRepository:VersionsRepository,
                     private val InstallationStatusRepository:InstallationStatusRepository,
                     private val userAgentRepository: userAgentRepository,
                     private  val  VipAccessRepository: VipAccessRepository
) {

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

    @GetMapping("/get_last_versions")
    fun getLastVersion():Versions {
        return if (VersionsRepository.findFirstByOrderByIdDesc().version_number.isNullOrEmpty()) Versions(0,"")
        else VersionsRepository.findFirstByOrderByIdDesc()
    }

    @GetMapping("/get_last_24h_run")
    fun getLast24hRun(): Any {
        val unixTime = System.currentTimeMillis() / 1000L - 24 * 60 * 60
        return Collections.singletonMap("Times_Run_Last_24h", InstallationStatusRepository.findByLast_startGreaterThanEqual(unixTime).size)
    }

    @PostMapping("/set_user_agent")
    fun setUserAgent(@Valid @RequestBody data: userAgent) {
        if (UniqueInstallationIdRepository.existsByUIDIs(UID = data.UID)) {
            if (userAgentRepository.existsByUID(UID = data.UID)) {
                userAgentRepository.updateUser_agentByUID(user_agent = data.user_agent, UID = data.UID)
            } else userAgentRepository.save(data)
        }
    }

    @GetMapping("/get_status_versions")
    fun getStatusVersions(): Any {
        val versions: List<Versions> = VersionsRepository.findByOrderByIdAsc()
        val response: ArrayList<VersionStatusInstallation> = ArrayList()
        for (element in versions) {
            val count: Long = InstallationStatusRepository.countByVersion_numberIs(element.version_number)
            response.add(VersionStatusInstallation(element.version_number,count = count))
        }
        return response
    }

    @GetMapping("/get_status_agents")
    fun getStatusAgents(): ArrayList<AgentsStatusInstallation> {
        val response: ArrayList<AgentsStatusInstallation> = ArrayList()
        var windows: Long = 0
        var windowsMobile: Long = 0
        var android: Long = 0
        var androidLinux: Long = 0
        var iphone: Long = 0
        val query: List<userAgent> = userAgentRepository.findByOrderByIdAsc()
        for (element in query) {
            if (element.user_agent.indexOf("(Linux; Android", 0) != -1) {
                androidLinux ++
            }
            if (element.user_agent.indexOf("(Android", 0) != -1) {
                android ++
            }
            if (element.user_agent.indexOf("(iPhone;", 0) != -1) {
                iphone ++
            }
            if (element.user_agent.indexOf("(Windows NT", 0) != -1) {
                windows ++
            }
            if (element.user_agent.indexOf("(Windows Phone", 0) != -1) {
                windowsMobile ++
            }
        }
        response.add(AgentsStatusInstallation(AgentName = "Windows", count = windows))
        response.add(AgentsStatusInstallation(AgentName = "Windows mobile", count = windowsMobile))
        response.add(AgentsStatusInstallation(AgentName = "Android", count = android))
        response.add(AgentsStatusInstallation(AgentName = "Android Linux", count = androidLinux))
        response.add(AgentsStatusInstallation(AgentName = "iphone", count = iphone))
        return response
    }

    @PostMapping("/install_Param")
    fun setLastStart(@Valid @RequestBody data: InstallationStatus): ResponseEntity<InstallationStatus> {
        if (UniqueInstallationIdRepository.existsByUIDIs(UID = data.UID)) {
            val version = VersionsRepository.findFirstByOrderByIdDesc().version_number
            val unixTime = System.currentTimeMillis() / 1000L
            return if (InstallationStatusRepository.existsByUID(data.UID)){
                InstallationStatusRepository.updateLast_startByUID(UID = data.UID, last_start = unixTime)
                InstallationStatusRepository.updateVersion_numberByUID(UID = data.UID, version_number = version)
                ResponseEntity(InstallationStatusRepository.findByUID(data.UID), HttpStatus.OK)
            }
            // updateLast_startByUID(last_start = data.last_start, UID = data.UID)
            else {
                data.last_start = unixTime
                data.version_number = version
                InstallationStatusRepository.save(data)
                ResponseEntity(data, HttpStatus.OK)
            }
        } else {
            return ResponseEntity(data, HttpStatus.CONFLICT)
        }
    }

    @PostMapping("/get_permissions")
    fun getPermissions(@RequestBody uid: Uid):Any {
        return if (VipAccessRepository.existsByUid(uid = uid.uid)) {
            VipAccessRepository.findByUid(uid = uid.uid)
        } else {
            ResponseEntity("Not found in permissions table", HttpStatus.NOT_FOUND)
        }
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
    @Value("\${spring.influx.token}")
    private val token: String? = null
    //@CrossOrigin(origins = ["http://localhost:8082"])
    // TODO: DELETE IT IF WILL BE NOT USED
    @PostMapping("/module_clicked")
    fun putModuleCLicked(@Valid @RequestBody data: ModuleClicked){
        data.moduleName = data.moduleName.lowercase()
        if (data.moduleName.indexOf(".pdf", 0) > 0){
            write("used_pdf",data.moduleName)
        } else {
            write("used_module",data.moduleName)
        }

        return
    }
    fun write(bucket: String, module: String) = runBlocking {

        // You can generate an API token from the "API Tokens Tab" in the UI
        val org = "Tapo24"

        val client = InfluxDBClientKotlinFactory.create("http://51.83.179.244:8062", token?.toCharArray() ?: "".toCharArray(), org, bucket)
        client.use {
            val point = Point
                .measurement("clicked_on")
                .addTag("module_name",module)
                .addField(module, 1)
                .time(Instant.now(), WritePrecision.NS)
            val writeApi = client.getWriteKotlinApi()
            writeApi.writePoint(point)
        }
        client.close()

    }
}
@CrossOrigin(origins = ["*"])
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/postal_code")
class TapoPostalCode(private  val  ModuleClicked: ModuleClickedRepository) {
    @GetMapping("/get_by_city")
    fun getByCity(@RequestParam code: String):ResponseEntity<String>{
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://polish-zip-codes1.p.rapidapi.com/${code}"))
            .setHeader("x-rapidapi-host", "polish-zip-codes1.p.rapidapi.com")
            .setHeader("x-rapidapi-key", "c12d626061msh43653fb2a1f88cbp1d0a56jsndebedd586ca5")
            .build();

        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode()==404) {
            return ResponseEntity(response.body(), HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(response.body(), HttpStatus.OK)
    }
}