package com.nhietLab5.frontend.controllers;

import com.nhietLab5.backend.models.Candidate;
import com.nhietLab5.backend.models.Company;
import com.nhietLab5.backend.repositories.CandidateRepository;
import com.nhietLab5.backend.repositories.CompanyRepository;
import com.nhietLab5.backend.services.CandidateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateServices candidateServices;
    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/list")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/candidates";
    }

    @GetMapping("/candidates")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size,
                                          @RequestParam("companyId") Optional<Long> companyId) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Candidate> candidatePage= candidateServices.findAll(
                currentPage - 1,pageSize,"id","asc");
        model.addAttribute("candidatePage", candidatePage);
        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        Long id = companyId.orElse(null);
        model.addAttribute("company", companyRepository.findById(id).orElse(null));
        return "candidates/candidates-paging";
    }

    @GetMapping("/candidates/suitable")
    public String showSuitableCandidatesForJob(Model model,
                                               @RequestParam("jobName") String jobName,
                                               @RequestParam("companyId") Long companyId,
                                               @RequestParam("page") Optional<Integer> page,
                                               @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Candidate> candidatePage = candidateServices.findSuitableCandidatesForJob(
                jobName, companyId, currentPage - 1, pageSize, "id", "asc");
        model.addAttribute("candidatePage", candidatePage);
        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("jobName", jobName);
        model.addAttribute("company", companyRepository.findById(companyId).orElse(null));
        return "candidates/candidatesSuitable";
    }
}
