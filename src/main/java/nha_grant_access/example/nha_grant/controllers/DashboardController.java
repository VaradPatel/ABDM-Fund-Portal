package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.Interface.IUserService;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import nha_grant_access.example.nha_grant.repository.*;
import nha_grant_access.example.nha_grant.service.GrantRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    IUserService iUserService;

    @Autowired
    IGrantRequests iGrantRequests;
    @Autowired
    IWorkFlowConfRepo iWorkFlowConfRepo;
    @Autowired
    GrantRequestService grantRequestService;

    @GetMapping("/sha-finance/{userId}")
    @PreAuthorize("hasAuthority('SHA Finance Division Individual')")
    public ResponseEntity<?> getShaFinancedashboard(@PathVariable("userId") Integer userId) {
        try {

            List<Object[]> results = iGrantRequestsRepo.shaFinanceDashboardDetails(userId);
            Object[] result = results.get(0);
            DashboardShaFin dashboardShaFin = DashboardShaFin.builder()
                    .totalAmountRequested(result[0] != null ? (BigDecimal) result[0] : BigDecimal.ZERO)
                    .totalAmountReleased(result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO)
                    // .maximumEligibleGrant(result[2] != null ? (BigDecimal) result[2] : BigDecimal.ZERO)
                    .completedProposals(result[2] != null ? ((Number) result[2]).intValue() : 0)
                    .pendingProposals(result[3] != null ? ((Number) result[3]).intValue() : 0)
                    .pendingQueries(result[4] != null ? ((Number) result[4]).intValue() : 0)
                    .respondedQueries(result[5] != null ? ((Number) result[5]).intValue() : 0)
                    .build();
            return ResponseEntity.ok().body(dashboardShaFin);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch the dashboard Records ", e.toString()));
        }

    }

    @GetMapping("/user-management/{stateId}")
    //@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> getUserApprovalByStateCeo(@PathVariable("stateId") Integer stateId) throws AccessDeniedException {
        try {


            List<Object[]> user = userRepo.findUsersRequestByStateId(stateId);

            List<UserRequest> users = user.stream()
                    .map(row -> UserRequest.builder()
                            .id((Integer) row[0])                 // u.id
                            .email((String) row[1])               // u.email
                            .mobile((String) row[2])              // u.mobile_number
                            .designation((String) row[3])         // u.designation
                            .stateName((String) row[4])           // s.name (state_name)
                            .roleName((String) row[5])
                            .name((String) row[6])
                            .createdAt((Date) row[7])
                            .isVerified((Boolean) row[8])
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(users);


        } catch (Exception e) {
            log.info("error occured while fetching user-approval" + e.toString());
            return ResponseEntity.ok().body(new Error("error occured while fetching user-approval", e.toString()));
        }


    }

    @GetMapping("/admin-user-management/{status}")
    //@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> getUserApprovalByAdmin(@PathVariable("status") Integer status) throws AccessDeniedException {
        try {
            List<Object[]> user = null;

            if (status != 0) {
                user = userRepo.findUsersRequestByAdmin(true);
            } else
                user = userRepo.findUsersRequestByAdmin(false);

            List<UserRequest> users = user.stream()
                    .map(row -> UserRequest.builder()
                            .id((Integer) row[0])                 // u.id
                            .email((String) row[1])               // u.email
                            .mobile((String) row[2])              // u.mobile_number
                            .designation((String) row[3])         // u.designation
                            .stateName((String) row[4])           // s.name (state_name)
                            .roleName((String) row[5])
                            .name((String) row[6])
                            .createdAt((Date) row[7])
                            .isVerified((Boolean) row[8])
                            .isActivated((Boolean) row[9])
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(users);


        } catch (Exception e) {
            log.info("error occured while fetching user-approval" + e.toString());
            return ResponseEntity.ok().body(new Error("error occured while fetching user-approval", e.toString()));
        }


    }


    @PostMapping("/user-approval")
    //@PreAuthorize("hasAuthority('State CEO') or hasAuthority('NHA admin')")

    public ResponseEntity<?> approveUser(@Valid @RequestBody UserApprovalRequest request) {
        try {

            if (iUserService.approveUser(request) > 0) {
                //sent sms ;
                return ResponseEntity.ok().body(new SuccessResponse("User Approval Updated Succesully"));
            } else {
                return ResponseEntity.badRequest().body("user not found");
            }

        } catch (Exception e) {
            log.error(" error while updating user approval requests " + e.toString());
            return ResponseEntity.internalServerError().body(new Error("error while updating user approval ", e.toString()));
        }

    }

//    @GetMapping("/sha-finance/{userId}")
//    public ResponseEntity<?> getstateCeodashboard(@PathVariable("userId") Integer userId) {
//        try
//        {
//
//            List<Object[]> results=iGrantRequestsRepo.shaFinanceDashboardDetails(userId);
//            Object[] result = results.get(0);
//            DashboardShaFin dashboardShaFin=DashboardShaFin.builder()
//                    .totalAmountRequested(result[0] != null ? (BigDecimal) result[0] : BigDecimal.ZERO)
//                    .totalAmountReleased(result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO)
//                    // .maximumEligibleGrant(result[2] != null ? (BigDecimal) result[2] : BigDecimal.ZERO)
//                    .completedProposals(result[2] != null ? ((Number) result[2]).intValue() : 0)
//                    .pendingProposals(result[3] != null ? ((Number) result[3]).intValue() : 0)
//                    .pendingQueries(result[4] != null ? ((Number) result[4]).intValue() : 0)
//                    .respondedQueries(result[5] != null ? ((Number) result[5]).intValue() : 0)
//                    .build();
//            return ResponseEntity.ok().body(dashboardShaFin);
//
//        }
//        catch(Exception e)
//        {
//            return ResponseEntity.internalServerError().body(new Error("Failed to fetch the dashboard Records ",e.toString()));
//        }
//
//    }

    @GetMapping("/stateceo-review/{stateId}")
    //@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> getstateCeoReview(@PathVariable("stateId") Integer stateId) throws AccessDeniedException {

        try {

            List<Object[]> results = iGrantRequestsRepo.findAllGrantRequestByStatus(stateId, 2);

            List<ReviewProposal> result = results.stream().map(obj -> new ReviewProposal(
                    (String) obj[0],   // request_id
                    (String) obj[1],   // remarks
                    (BigDecimal) obj[2],   // requested_amount
                    (String) obj[3],
                    (Date) obj[4],
                    (String) obj[5]// user_name
            )).toList();
            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            //log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Failed while fetching review data ", e.toString()));
        }
    }

    @GetMapping("/state-ceo")
    public ResponseEntity<?> getStateCeoDashboard(
            @RequestParam Integer stateId,
            @RequestParam Integer userId,
            @RequestParam String policyStartDate,
            @RequestParam String policyEndDate
            ) {
        try {


            List<Object[]> results = iGrantRequestsRepo.getStateCeoDashboard(stateId, userId,policyStartDate, policyEndDate);

            if (results.isEmpty()) {
                return ResponseEntity.ok().body(new StateCeoDashboatd());
            }

            Object[] row = results.get(0);

            StateCeoDashboatd result = StateCeoDashboatd.builder()
                    .totalRequestedAmount((BigDecimal) row[0])
                    .totalReleasedAmount((BigDecimal) row[1])
                    .nhaReview((Long) row[2])
                    .pendingProposals((Long) row[3])
                    .pendingQuery((Long) row[4])
                    .shaPending((Long) row[5])
                    .resolvedQuery((Long) row[6])
                    .build();
            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while fetching dashboard details", e.toString()));
        }
    }

    @GetMapping("/statecord/{stateId}")
    public ResponseEntity<?> getStateCordDashboard(@PathVariable("stateId") Integer stateId) {
        try {


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            User user = userRepo.findByEmail(name).get();


            List<Integer> StateIds = userRepo.findStateIdByRole(3, user.getId());
            List<Object[]> results = null;
            if (stateId != 0) {
                System.out.println("states " + stateId);
                results = iGrantRequestsRepo.getStateCordDashboard(user.getId(), Collections.singletonList(stateId));
            } else {
                System.out.println("states" + StateIds.toString());
                results = iGrantRequestsRepo.getStateCordDashboard(user.getId(), StateIds);
            }
            List<StateCordDashboardResponse> responseList = results.stream()
                    .map(row -> StateCordDashboardResponse.builder()
                            .totalRequestedAmount((BigDecimal) row[0])
                            .totalReleasedAmount((BigDecimal) row[1])
                            .pending(((Number) row[2]).intValue())
                            .pendingForSanction(((Number) row[3]).intValue())
                            .pendingAtNhareviewer(((Number) row[4]).intValue())
                            .sanctionUpload(((Number) row[5]).intValue())
                            .pendingQuery(((Number) row[6]).intValue())
                            .queryRaised(((Number) row[7]).intValue())
                            .resolvedQuery(((Number) row[8]).intValue())
                            .maxEligibleGrants((BigDecimal) row[9] )
                            .build()
                    ).toList();

            return ResponseEntity.ok().body(responseList);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while fetching dashboard details", e.toString()));

        }
    }


    @GetMapping("/statecord-review/{stateId}")
    //@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> stateCordReview(@PathVariable("stateId") Integer stateId) throws AccessDeniedException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            List<Object[]> results = null;
            if (stateId != 0) {
                results = iGrantRequestsRepo.getGrantRequestsWithState(Collections.singletonList(stateId), 4,2);
            } else {
                User user = userRepo.findByEmail(name).get();


                List<Integer> StateIds = userRepo.findStateIdByRole(3, user.getId());
                System.out.println("stateIds " + StateIds.toString() + " user " + user.toString());
                results = iGrantRequestsRepo.getGrantRequestsWithState(StateIds, 4, 2);

            }
            List<ReviewProposal> result = results.stream().map(obj -> new ReviewProposal(
                    (String) obj[0],   // request_id
                    (String) obj[1],   // remarks
                    (BigDecimal) obj[2],   // requested_amount
                    (String) obj[3],
                    (Date) obj[4],
                    (String) obj[5] //
            )).toList();
            return ResponseEntity.ok().body(result);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while fetching dashboard details", e.toString()));
        }

    }

    @GetMapping("/statecord-all/{stateId}")
    //@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> stateCordAllProposal(@PathVariable("stateId") Integer stateId) throws AccessDeniedException {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            List<AllGrantRequest> allGrantRequests = null;
            if (stateId != 0) {
                allGrantRequests = iGrantRequests.getAllGrantRequestByState(Collections.singletonList(stateId), null);
                //results = iGrantRequestsRepo.getGrantRequestsWithState(Collections.singletonList(stateId), 4);
            } else {
                User user = userRepo.findByEmail(name).get();


                List<Integer> StateIds = userRepo.findStateIdByRole(3, user.getId());
                System.out.println("stateIds " + StateIds.toString() + " user " + user.toString());
                allGrantRequests = iGrantRequests.getAllGrantRequestByState(StateIds, null);

            }

            return ResponseEntity.ok().body(allGrantRequests);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while fetching  details", e.toString()));
        }

    }

    @PostMapping("/stateceo-approve")
    public ResponseEntity<?> StateCeoApproval(@Valid @RequestBody RequestId requestId) {
        {
            try {
                WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 2);
                iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
                grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
                return ResponseEntity.ok().body(new SuccessResponse("Request approved Successfully"));
            } catch (Exception e) {
                log.error(e.toString());
                return ResponseEntity.internalServerError().body(new Error("Error occured while Approving request", e.toString()));

            }
        }


    }
    @PostMapping("/statecord-approve")
    public ResponseEntity<?> StateCordApproval(@Valid @RequestBody RequestId requestId) {
        {
            try {
                WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 3);
                iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
                grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
                return ResponseEntity.ok().body(new SuccessResponse("Request approved Successfully"));
            } catch (Exception e) {
                log.error(e.toString());
                return ResponseEntity.internalServerError().body(new Error("Error occured while Approving request", e.toString()));

            }
        }


    }
    @PostMapping("/nhareviewer-approve")
    public ResponseEntity<?> nhaReviewerApproval(@Valid @RequestBody RequestId requestId) {
        {


            try {
                WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 4);
                iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
                grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
                return ResponseEntity.ok().body(new SuccessResponse("Request approved Successfully"));
            } catch (Exception e) {
                log.error(e.toString());
                return ResponseEntity.internalServerError().body(new Error("Error occured while Approving request", e.toString()));

            }
        }


    }
    @GetMapping("/upload-sanction-list")
    public ResponseEntity<?> uploadSanctionList() {
try
{
    List<Object[]> results = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String name = authentication.getName();
    User user = userRepo.findByEmail(name).get();


    List<Integer> StateIds = userRepo.findStateIdByRole(3, user.getId());
    System.out.println("state ids are " + StateIds.toString());
    results = iGrantRequestsRepo.getGrantRequestsWithState(StateIds, 8,4);
    List<ReviewProposal> result = results.stream().map(obj -> new ReviewProposal(
            (String) obj[0],   // request_id
            (String) obj[1],   // remarks
            (BigDecimal) obj[2],   // requested_amount
            (String) obj[3],
            (Date) obj[4],
            (String) obj[5] //
    )).toList();
    return ResponseEntity.ok().body(result);

}
catch(Exception e)
{
    log.error(e.toString());
    return ResponseEntity.internalServerError().body(new Error("Error occured while Approving request", e.toString()));

}

    }
@GetMapping("/nhareviewer-review")
//@PreAuthorize("hasAuthority('State CEO') ")
public ResponseEntity<?> NhaReviewerReview() throws AccessDeniedException {
try
{

    List<Object[]> results = null;
    results = iGrantRequestsRepo.getNhaReviewerPending(6);
            List<ReviewProposal> result = results.stream().map(obj -> new ReviewProposal(
            (String) obj[0],   // request_id
            (String) obj[1],   // remarks
            (BigDecimal) obj[2],   // requested_amount
            (String) obj[3],
            (Date) obj[4],
            (String) obj[5] //
    )).toList();
    return ResponseEntity.ok().body(result);
}
 catch (Exception e) {
        log.error(e.toString());
        return ResponseEntity.internalServerError().body(new Error("Error occured while loading data ", e.toString()));

    }

}
    @GetMapping("/nhareviewer-all")
//@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> NhaReviewerAll() throws AccessDeniedException {
        try
        {

        List<AllGrantRequest>
            allGrantRequests = iGrantRequests.getAllGrantRequest();
return ResponseEntity.ok().body(allGrantRequests);


        }
        catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while loading data ", e.toString()));

        }
    }
    @PostMapping("/nhaReviewer-approve")
    public ResponseEntity<?> NhaReviewerApproval(@Valid @RequestBody RequestId requestId) {
        {
            try {
                WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 4);
                iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
                grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
                return ResponseEntity.ok().body(new SuccessResponse("Request approved Successfully"));
            } catch (Exception e) {
                log.error(e.toString());
                return ResponseEntity.internalServerError().body(new Error("Error occured while Approving request", e.toString()));

            }
        }


    }
    @GetMapping("/nhareviewer/{stateId}")
    public ResponseEntity<?> getNhaReviewerDashboard(@PathVariable("stateId") Integer stateId) {
        try {


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            User user = userRepo.findByEmail(name).get();



            List<Object[]> results = null;
            if (stateId != 0) {
                System.out.println("states " + stateId);
                results = iGrantRequestsRepo.getNhaReviewerDashboard(user.getId(), stateId);
            } else {

                results = iGrantRequestsRepo.getNhaReviewerDashboard(user.getId(), 0);
            }
            List<NhaReviewerDashboard> responseList = results.stream()
                    .map(row -> NhaReviewerDashboard.builder()
                            .totalRequestedAmount((BigDecimal) row[0])
                            .totalReleasedAmount((BigDecimal) row[1])
                            .pending(((Number) row[2]).intValue())

                            .accepted(((Number) row[3]).intValue())

                            .pendingQuery(((Number) row[4]).intValue())
                            .queryRaised(((Number) row[5]).intValue())
                            .resolvedQuery(((Number) row[6]).intValue())
                            .totalMaxEligibleGrants((BigDecimal) row [7])
                            .build()
                    ).toList();

            return ResponseEntity.ok().body(responseList);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while fetching dashboard details", e.toString()));

        }
    }
    @GetMapping("/nhaAdmin/{stateId}")
    public ResponseEntity<?> getNhaAdmin(@PathVariable("stateId") Integer stateId) {
        try {


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            User user = userRepo.findByEmail(name).get();



            List<Object[]> results = null;
            if (stateId != 0) {
                System.out.println("states " + stateId);
                results = iGrantRequestsRepo.getNhaReviewerDashboard(user.getId(), stateId);
            } else {

                results = iGrantRequestsRepo.getNhaReviewerDashboard(user.getId(), 0);
            }
            List<NhaReviewerDashboard> responseList = results.stream()
                    .map(row -> NhaReviewerDashboard.builder()
                            .totalRequestedAmount((BigDecimal) row[0])
                            .totalReleasedAmount((BigDecimal) row[1])
                            .pending(((Number) row[2]).intValue())

                            .accepted(((Number) row[3]).intValue())

                            .pendingQuery(((Number) row[4]).intValue())
                            .queryRaised(((Number) row[5]).intValue())
                            .resolvedQuery(((Number) row[6]).intValue())
                            .build()
                    ).toList();

            return ResponseEntity.ok().body(responseList);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error occured while fetching dashboard details", e.toString()));

        }
    }

}