package com.nhietLab5.frontend.controllers;

import com.nhietLab5.backend.models.*;
import com.nhietLab5.backend.repositories.*;
import com.nhietLab5.backend.services.CandidateServices;
import com.nhietLab5.backend.services.EmailSenderServices;
import com.nhietLab5.backend.services.JobServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/candidate")
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateServices candidateServices;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CandidateSkillRepository candidateSkillRepository;
    @Autowired
    private ExperienceRepository experienceRepository;
    @Autowired
    private EmailSenderServices emailSenderServices;
    @Autowired
    private JobServices jobServices;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobSkillRepository jobSkillRepository;
    @Autowired
    private SkillRepository skillRepository;

//    @GetMapping("/list")
//    public String showCandidateListPaging(Model model,
//                                          @RequestParam("page") Optional<Integer> page,
//                                          @RequestParam("size") Optional<Integer> size,
//                                          @RequestParam("companyId") Optional<Long> companyId) {
//        int currentPage = page.orElse(1);
//        int pageSize = size.orElse(10);
//        Page<Candidate> candidatePage= candidateServices.findAll(
//                currentPage - 1,pageSize,"id","asc");
//        model.addAttribute("candidatePage", candidatePage);
//        int totalPages = candidatePage.getTotalPages();
//        if (totalPages > 0) {
//            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
//                    .boxed()
//                    .collect(Collectors.toList());
//            model.addAttribute("pageNumbers", pageNumbers);
//        }
//
//        Long id = companyId.orElse(null);
//        model.addAttribute("company", companyRepository.findById(id).orElse(null));
//        return "companies/candidates-paging";
//    }
//
//    @GetMapping("/suitable")
//    public String showSuitableCandidatesForJob(Model model,
//                                               @RequestParam("jobName") String jobName,
//                                               @RequestParam("companyId") Long companyId,
//                                               @RequestParam("page") Optional<Integer> page,
//                                               @RequestParam("size") Optional<Integer> size) {
//        int currentPage = page.orElse(1);
//        int pageSize = size.orElse(10);
//        Page<Candidate> candidatePage = candidateServices.findSuitableCandidatesForJob(
//                jobName, companyId, currentPage - 1, pageSize, "id", "asc");
//        model.addAttribute("candidatePage", candidatePage);
//        int totalPages = candidatePage.getTotalPages();
//        if (totalPages > 0) {
//            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
//                    .boxed()
//                    .collect(Collectors.toList());
//            model.addAttribute("pageNumbers", pageNumbers);
//        }
//        model.addAttribute("jobName", jobName);
//        model.addAttribute("company", companyRepository.findById(companyId).orElse(null));
//        return "companies/candidatesSuitable";
//    }
//
//    @GetMapping("/sendEmail")
//    public String sendEmail(@RequestParam("compEmail") String compEmail,
//                            @RequestParam("canEmail") String canEmail) {
//        Candidate candidate = candidateRepository.findByEmail(canEmail);
//        Company company = companyRepository.findByEmail(compEmail);
//        try {
//            emailSenderServices.sendEmail(
//                    compEmail,
//                    canEmail,
//                    "Job Offer",
//                    "Dear " + candidate.getFullName() + ",\n" +
//                            "We are pleased to offer you a job at " + company.getCompName() + ".\n" +
//                            "Please contact us for more information.\n" +
//                            "Best regards,\n" +
//                            company.getCompName() + "."
//            );
//        }catch (Exception e){
//            e.printStackTrace();
//            return "redirect:/candidates/list?companyId=" + company.getId();
//        }
//
//
//        return "redirect:/candidates/list?companyId=" + company.getId();
//    }

    @GetMapping("/jobList")
    public String showJobListPaging(Model model,
                                    @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size,
                                    @RequestParam("candidateId") Optional<Long> candidateId) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Job> jobPage = jobServices.findAll(
                currentPage - 1, pageSize, "id", "asc");
        model.addAttribute("jobPage", jobPage);
        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        Long id = candidateId.orElse(null);
        model.addAttribute("candidate", candidateRepository.findById(id).orElse(null));
        return "candidates/jobs";
    }

    @GetMapping("/jobDetail")
    public String showJobDetail(Model model,
                                @RequestParam("jobId") String id,
                                @RequestParam("companyId") String companyId,
                                @RequestParam(value="candidateId", required = false) Optional<Long> candidateId) {
        Job job = jobRepository.findById(Long.parseLong(id)).orElse(null);
        Long candidateID = candidateId.orElse(null);
        List<JobSkill> jobSkill = jobSkillRepository.findAllByJobId(Long.parseLong(id));
        job.setJobSkills(jobSkill);
        model.addAttribute("job", job);
        model.addAttribute("company", companyRepository.findById(Long.parseLong(companyId)).orElse(null));
        if (candidateId.isPresent()) {
            model.addAttribute("candidate", candidateRepository.findById(candidateID).orElse(null));
            return "candidates/jobDetails";
        }
        return "companies/jobDetails";
    }

    @GetMapping("/detail")
    public String showCandidateDetail(Model model,
                                      @RequestParam("canID") Long canID,
                                      @RequestParam(value = "compID", required = false) Long compID) {
        Candidate candidate = candidateRepository.findById(canID).orElse(null);
        model.addAttribute("candidate", candidate);

        Experience experience = experienceRepository.findByCandidate(candidate);
        model.addAttribute("experience", experience);

        List<CandidateSkill> candidateSkills = candidateSkillRepository.findByCan(candidate);
        model.addAttribute("candidateSkills", candidateSkills);

        if(compID != null){
            model.addAttribute("company", companyRepository.findById(compID).orElse(null));
            return "companies/candidateDetail";
        }

        return "candidates/candidateDetail";
    }

    @GetMapping("/suitableJobs")
    public String showSuitableJobs(Model model,
                                   @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size,
                                   @RequestParam("candidateId") String candidateId) {
        Long id = Long.parseLong(candidateId);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Job> jobPage = jobServices.findSuitableJobsForCandidate(
                currentPage - 1, pageSize, "id", "asc", id);
        model.addAttribute("jobPage", jobPage);
        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("candidate", candidateRepository.findById(id).orElse(null));
        return "candidates/jobsSuitable";
    }

    @GetMapping("/searchJobs")
    public String searchJob(Model model,
                            @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size,
                            @RequestParam("candidateId") String candidateId,
                            @RequestParam("condition") String condition,
                            @RequestParam("inputValue") String input) {
        Long id = Long.parseLong(candidateId);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        if(condition.equals("jobName")){
            Page<Job> jobPage = jobServices.findJobsByJobName(
                    currentPage - 1, pageSize, "id", "asc", input);
            model.addAttribute("jobPage", jobPage);
            int totalPages = jobPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
        } else if(condition.equals("company")){
            Page<Job> jobPage = jobServices.findJobsByCompany_CompName(
                    currentPage - 1, pageSize, "id", "asc", input);
            model.addAttribute("jobPage", jobPage);
            int totalPages = jobPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }
        model.addAttribute("candidate", candidateRepository.findById(id).orElse(null));
        return "candidates/jobs";
    }

    @GetMapping("/missingSkills")
    public String getSkillsMissing(Model model,
                                   @RequestParam("candidateId") Optional<Long> candidateId,
                                   @RequestParam("jobName") Optional<String> jobName,
                                   @RequestParam("companyName") Optional<String> companyName) {
        Long canId = candidateId.orElse(null);
        String sJobName = jobName.orElse(null);
        String sCompanyName = companyName.orElse(null);

        model.addAttribute("candidate", candidateRepository.findById(canId).orElse(null));

        List<Skill> skills = skillRepository.findMissingSkillsForCandidateByJobAndCompany(sJobName, sCompanyName, canId);
        model.addAttribute("skillsMissing", skills);

        Job job = jobRepository.findByJobNameAndCompany_CompName(sJobName, sCompanyName);
        model.addAttribute("job", job);

        Map<Long, JobSkill> skillJobSkillMapById = new HashMap<>();
        for (Skill skill : skills) {
            JobSkill jobSkill = jobSkillRepository.findByJobAndSkill(job, skill);
            if (jobSkill != null) {
                skillJobSkillMapById.put(skill.getId(), jobSkill);
            }
        }
        model.addAttribute("skillJobSkillMapById", skillJobSkillMapById);

        return "candidates/skillsMissing";
    }
}
