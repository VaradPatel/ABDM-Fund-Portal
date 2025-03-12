package nha_grant_access.example.nha_grant.controllers;

import lombok.AllArgsConstructor;
import nha_grant_access.example.nha_grant.dto.DashboardShaFin;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    IGrantRequestsRepo  iGrantRequestsRepo;

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
}
