package com.lepse.integration.controllers;

import com.jcraft.jsch.JSchException;
import com.lepse.integration.dao.MaxDAO;
import com.lepse.integration.models.Imast;
import com.lepse.integration.models.MaxResponse;
import com.lepse.integration.models.PstModel;
import com.lepse.integration.services.FileStorageService;
import com.lepse.integration.services.SshProcedureCallService;
import com.lepse.integrations.dao.OperationStatus;
import com.lepse.integrations.log.LogRecord;
import com.lepse.integrations.log.LogsDAO;
import com.lepse.integrations.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * the controller class that describes the REST api
 */
@RestController
@RequestMapping("/max")
public class MaxController {
    private static final String commandBatch = "/max/etc/batch/_max_run.sh";
    private static final String commandRecord = "/max/etc/batch/_max_run_p.sh";

    private final FileStorageService fileStorageService;
    private final SshProcedureCallService sshProcedureCallService;
    private final LogsDAO logsDAO;
    private final MaxDAO maxDAO;
    private String logMessage = "";
    private String message = "";

    private static final Logger logger = LoggerFactory.getLogger(MaxController.class);

    /**
     * creates a new instance of the controller and embeds dependencies on services and DAO
     * @param fileStorageService a service that saves files and stores them
     * @param sshProcedureCallService a service that provides a procedure call over an ssh connection
     * @param logsDAO logs data access object
     */
    @Autowired
    public MaxController(FileStorageService fileStorageService, SshProcedureCallService sshProcedureCallService, LogsDAO logsDAO, MaxDAO maxDAO) {
        this.fileStorageService = fileStorageService;
        this.sshProcedureCallService = sshProcedureCallService;
        this.logsDAO = logsDAO;
        this.maxDAO = maxDAO;
    }

    /**
     * POST request controller for upload files and call a procedure via an ssh connection above them
     * @param files array of files
     * @param sender the sender of the request to this API
     * @return returns the response body
     */
    @PostMapping("/upload")
    @ResponseBody
    public MaxResponse upload(@RequestParam("files") MultipartFile[] files, @RequestParam("sender") String sender,
                              @RequestParam("integration_name") String integrationName){
        BaseResponse baseResponse = null;
        LogRecord logRecord = new LogRecord.LogRecordBuilder()
                .setDate(new Date())
                .setIntegrationName(integrationName)
                .setRequestType(LogRecord.RequestType.POST)
                .setSender(sender)
                .build();
        try {
            for (MultipartFile file : files) {
                String fileName = fileStorageService.storeFile(file);
                String sshResponse = "";

                if (fileName.contains(".rec")) {
                    sshResponse = sshProcedureCallService.run(commandRecord + " " + fileName);
                    if (!sshResponse.contains(ResponseMessages.RESPONSE_CORRECT_EM41.getMessage())) {
                        baseResponse = getErrorResponse(sshResponse, logRecord);
                        return new MaxResponse(baseResponse.getCode(), baseResponse.getStatus());
                    }
                } else {
                    sshResponse = sshProcedureCallService.run(commandBatch + " " + fileName);
                }

                if (!sshResponse.contains(ResponseMessages.RESPONSE_CORRECT_IM40.getMessage())) {
                    baseResponse = getErrorResponse(sshResponse, logRecord);
                    return new MaxResponse(baseResponse.getCode(), baseResponse.getStatus());
                }
                message += sshResponse;
            }
            logger.info(message);

            logRecord.setMessage(message);
            baseResponse = logsDAO.save(logRecord, OperationStatus.SUCCESS);
        }
        catch (IOException | InterruptedException | JSchException exception){
            logMessage = exception.getMessage();

            logger.error(logMessage);

            logRecord.setMessage(message);
            baseResponse = logsDAO.save(logRecord, OperationStatus.ERROR);
        }
        finally {
            if (baseResponse.getCode().equals(BaseResponse.Code.LOG_RW_WARN_CODE))
                logger.warn(message);
        }

        return new MaxResponse(baseResponse.getCode(), baseResponse.getStatus());
    }

    /**
     * GET request controller to get information from the IMAST table about an item by item id
     * @param itemId item id
     * @return returns the Imast instance
     * */
    @GetMapping("/imast/{itemId}")
    public Imast getItem(@PathVariable String itemId) {
        try {
            List<Imast> items = maxDAO.getItem(itemId);
            return !items.isEmpty() ? items.get(0) : new Imast();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Imast();
    }

    /**
     * GET request controller to get information from the EMOD table about notification number by parent item id and date of implementation
     * @param parentId parent item id
     * @param effective date of implementation
     * @return notification number
     * */
    @GetMapping("/emod/{parentId}/{effective}")
    public String getEmodModser(@PathVariable String parentId, @PathVariable String effective) {
        return maxDAO.getEmodModser(parentId, effective);
    }

    /**
     * GET request controller to get information from the MMSER table about last notification number
     * @return last notification number
     * */
    @GetMapping("/emod")
    public String getLastEmodModser() {
        return maxDAO.getLastEmodModser();
    }

    /**
     * GET request controller to get information from the PST table about structure by parent item id
     * @param parentId parent item id
     * @return structure items
     * */
    @GetMapping({"/pst/{parentId}"})
    public PstModel getPst(@PathVariable String parentId) {
        try {
            PstModel pstModel = this.maxDAO.getPst(parentId);
            return !pstModel.getItems().isEmpty() ? pstModel : new PstModel();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return new PstModel();
        }
    }

    private BaseResponse getErrorResponse(String sshResponse,  LogRecord logRecord) {
        logger.info(sshResponse);
        logRecord.setMessage(sshResponse);
        return logsDAO.save(logRecord, OperationStatus.ERROR);
    }
}
