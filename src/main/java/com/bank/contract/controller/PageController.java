package com.bank.contract.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.contract.entity.Contract;
import com.bank.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 页面控制器
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    private final ContractService contractService;

    /**
     * 合同列表页面
     */
    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status,
            Model model) {

        Page<Contract> page = contractService.getContractList(pageNum, pageSize, contractNo, customerName, status);
        model.addAttribute("page", page);
        model.addAttribute("contractNo", contractNo);
        model.addAttribute("customerName", customerName);
        model.addAttribute("status", status);

        return "index";
    }

    /**
     * 新建合同页面
     */
    @GetMapping("/contracts/new")
    public String newContract(Model model) {
        model.addAttribute("contract", new Contract());
        return "contract_form";
    }

    /**
     * 编辑合同页面
     */
    @GetMapping("/contracts/{id}/edit")
    public String editContract(@PathVariable Long id, Model model) {
        Contract contract = contractService.getContractById(id);
        if (contract == null) {
            return "redirect:/";
        }
        model.addAttribute("contract", contract);
        return "contract_form";
    }

    /**
     * 保存合同（新建/编辑共用）
     */
    @PostMapping("/contracts")
    public String saveContract(@ModelAttribute Contract contract, Model model) {
        try {
            if (contract.getId() != null) {
                contractService.updateContract(contract);
            } else {
                contractService.createContract(contract);
            }
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contract", contract);
            return "contract_form";
        }
    }

    /**
     * 合同详情页面
     */
    @GetMapping("/contracts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Contract contract = contractService.getContractById(id);
        if (contract == null) {
            return "redirect:/";
        }
        model.addAttribute("contract", contract);
        return "contract_detail";
    }

    /**
     * 删除合同
     */
    @GetMapping("/contracts/{id}/delete")
    public String delete(@PathVariable Long id) {
        contractService.deleteContract(id);
        return "redirect:/";
    }
}
