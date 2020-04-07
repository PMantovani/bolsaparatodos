package com.monetovani.monetovanisrv.controller;

import com.monetovani.monetovanisrv.entity.financial.Asset;
import com.monetovani.monetovanisrv.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping
    public void syncAssets(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean purgeExistent) {

        date = date != null ? date : LocalDate.now();

        this.assetService.syncAssets(date, purgeExistent);
    }

    @GetMapping
    public List<Asset> getAssets() {
        return this.assetService.getAllAssets();
    }
}
