package nha_grant_access.example.nha_grant.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IUserService;
import nha_grant_access.example.nha_grant.dto.DashboardShaFin;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.UserApprovalRequest;
import nha_grant_access.example.nha_grant.dto.UserRequest;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IactionRepository;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {
    @Autowired
    IGrantRequestsRepo  iGrantRequestsRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    IUserService iUserService;

    @GetMapping("/sha-finance/{userId}")
    public ResponseEntity<?> getShaFinancedashboard(@PathVariable("userId") Integer userId) {
     try
     {
         List<Object[]> results=iGrantRequestsRepo.shaFinanceDashboardDetails(userId);
         Object[] result = results.get(0);
         DashboardShaFin dashboardShaFin=DashboardShaFin.builder()
                 .totalAmountRequested(result[0] != null ? (BigDecimal) result[0] : BigDecimal.ZERO)
                 .totalAmountReleased(result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO)
                // .maximumEligibleGrant(result[2] != null ? (BigDecimal) result[2] : BigDecimal.ZERO)
                 .completedProposals(result[2] != null ? ((Number) result[2]).intValue() : 0)
                 .pendingProposals(result[3] != null ? ((Number) result[3]).intValue() : 0)
                 .pendingQueries(result[4] != null ? ((Number) result[4]).intValue() : 0)
                 .respondedQueries(result[5] != null ? ((Number) result[5]).intValue() : 0)
                 .build();
         return ResponseEntity.ok().body(dashboardShaFin);

     }
     catch(Exception e)
     {
         return ResponseEntity.internalServerError().body(e.toString());
     }

    }
    @GetMapping("/user-management/{stateId}")
    public ResponseEntity<?> getUserApprovalByStateCeo(@PathVariable("stateId") Integer stateId) {
        try {


            List<Object[]> user = userRepo.findUsersRequestByStateId(stateId);

            List<UserRequest> users = user.stream()
                    .map(row -> UserRequest.builder()
                            .id((Integer) row[0])                 // u.id
                            .email((String) row[1])               // u.email
                            .mobile((String) row[2])              // u.mobile_number
                            .designation((String) row[3])         // u.designation
                            .stateName((String) row[4])           // s.name (state_name)
                            .roleName((String) row[5])            // r.name (role_name)
                            .build())
                    .collect(Collectors.toList());
 return ResponseEntity.ok().body(users);


        }
        catch(Exception e)
        {
            log.info("error occured while fetching user-approval" + e.toString());
            return ResponseEntity.ok().body(new Error("error occured while fetching user-approval",e.toString()));
        }


       }



    @PostMapping("/user-approval")
    public ResponseEntity<?> approveUser(@RequestBody UserApprovalRequest request) {
try
{

    if(iUserService.approveUser(request)>0) {
        return ResponseEntity.ok().body("User Approval Updated Succesully");
    }
    else {
        return ResponseEntity.badRequest().body("user not found");
    }

}
catch (Exception e)
{
    log.error("error while updating user approval requets " +e.toString());
    return ResponseEntity.internalServerError().body(new Error("error while updating user approval ", e.toString()));
}

    }


}
