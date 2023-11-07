package com.frpr.controlller;

import com.frpr.model.Action;
import com.frpr.model.FacilityRegistry;
import com.frpr.model.ProviderRegistry;
import com.frpr.repo.*;
import com.frpr.response.CommonResponse;
import com.frpr.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/v1/facility-registry")
public class FacilityRegistryController {

    @Autowired
    FacilityRepository repository;

    @Autowired
    CellsRepository cellsRepository;

    @Autowired
    DistrictsRepository districtsRepository;

    @Autowired
    ProvincesRepository provincesRepository;

    @Autowired
    SectorsRepository sectorsRepository;

    @Autowired
    VillagesRepository villagesRepository;


    @Autowired
    RestTemplate restTemplate;

    @Value("${fr.url}")
    private String frBaseUrl;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuditService auditService;

    private String getLoggedInUser() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

   /* @PostMapping
    public ResponseEntity<CommonResponse> save(@RequestBody FacilityRegistry user) {
        //     user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        FacilityRegistry u = repository.save(user);
        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(u), HttpStatus.ACCEPTED);
    }*/

    /*@PutMapping
    public ResponseEntity<CommonResponse> update(@RequestBody FacilityRegistry user) {
        FacilityRegistry u = repository.save(user);
        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(u), HttpStatus.ACCEPTED);
    }*/

    /*@GetMapping("/{id}")
    public ResponseEntity<CommonResponse> getOnce(@PathVariable String id) {
        Optional<FacilityRegistry> u = repository.findById(id);

        FRPojo pojo = new FRPojo(u.get());
        try {
            pojo.setCell(cellsRepository.findById(u.get().getCell()).get());
            pojo.setDistrict(districtsRepository.findById(u.get().getDistrict()).get());
            pojo.setProvince(provincesRepository.findById(u.get().getProvince()).get());
            pojo.setSector(sectorsRepository.findById(u.get().getSector()).get());
            pojo.setVillage(villagesRepository.findById(u.get().getVillage()).get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(pojo), HttpStatus.ACCEPTED);
    }*/

    @GetMapping("/activeList")
    public ResponseEntity<CommonResponse> getAll(@RequestParam(required = false) String name, HttpServletRequest request) {

        auditService.saveAuditLog(getLoggedInUser(), FacilityRegistry.class.getSimpleName(), Action.ADD, name, null, request);
        ProviderRegistry providerRegistry = new ProviderRegistry();
        providerRegistry.setFacilityId(Collections.singletonList("1234"));
        List<FacilityRegistry> registries = getFR(providerRegistry);

        if( name != null){
            //Here try to filter the list to be return to the frontend User
            List<FacilityRegistry> filteredRegistries = new ArrayList<>();
            for (FacilityRegistry reg: registries ) {
                if(reg.getName() != null && reg.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase())){
                    reg.setName(reg.getName() + " - " + reg.getCode());
                    filteredRegistries.add(reg);
                }
            }
            return new ResponseEntity<>(new CommonResponse("ok", null).setData(filteredRegistries).setCount( (long) filteredRegistries.size() ), HttpStatus.ACCEPTED);
        }
        for(int i=0; i < registries.size(); i++){
            registries.get(i).setName(registries.get(i).getName() + " - " + registries.get(i).getCode());
        }
        return new ResponseEntity<CommonResponse>(new CommonResponse("ok", null).setData(registries).setCount((long) registries.size()), HttpStatus.ACCEPTED);
    }

    private List<FacilityRegistry> getFR(ProviderRegistry registry) {
        String url = frBaseUrl + "/api/v1/facility-registry/byids";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<ProviderRegistry> entity = new HttpEntity<ProviderRegistry>(registry, headers);

        return Objects.requireNonNull(restTemplate.exchange(
                url, HttpMethod.POST, entity, new ParameterizedTypeReference<List<FacilityRegistry>>() {
                }).getBody());

    }
}
