package com.hendisantika.springboot.swagger.controller;

import com.hendisantika.springboot.swagger.graph.Constants;
import com.hendisantika.springboot.swagger.graph.Graph;
import com.hendisantika.springboot.swagger.graph.Authentication;
import com.hendisantika.springboot.swagger.graph.Authenticator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.DefaultClientConfig;
import com.microsoft.graph.core.IClientConfig;
import com.microsoft.graph.httpcore.ICoreAuthenticationProvider;
import com.microsoft.graph.models.extensions.Attachment;
import com.microsoft.graph.models.extensions.DriveItem;
import com.microsoft.graph.models.extensions.Group;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.Multipart;
import com.microsoft.graph.models.extensions.Notebook;
import com.microsoft.graph.models.extensions.OnenotePage;
import com.microsoft.graph.models.extensions.OnenoteSection;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IDriveItemCollectionPage;
import com.microsoft.graph.requests.extensions.IGroupCollectionPage;

import com.hendisantika.springboot.swagger.model.Student;
import com.hendisantika.springboot.swagger.service.StudentService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by hendisantika on 4/24/17.
 */

@RestController
@RequestMapping(value = "/students")
@Api(value = "API to search Student from a Student Repository by different search parameters",
        description = "This API provides the capability to search Student from a Student Repository", produces = "application/json")
public class StudentController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StudentService studentService;

    @ApiOperation(value = "Test Graph API", produces = "application/json")
    @RequestMapping(value = "/test-graph", method = RequestMethod.GET)
    public ResponseEntity<Object> testGraphAPI() throws Exception {
        logger.debug("Getting All students ......");
        List<Student> student = null;

		//Get an access token for the client
        // Authentication.initialize(Constants.clientId, Constants.authority);
        final String accessToken = Authentication.getAccessTokenByClientCredentialGrant().accessToken();
        System.out.println("Token: " + accessToken);

        // System.out.println("Users: " + Authentication.getUsersListFromGraph(accessToken));
        // Greet the user
        int test = Graph.getMail(accessToken).getCurrentPage().size();
        System.out.println("Inbox count: " + test);
        System.out.println();

        // IGraphServiceClient graphClient = GraphServiceClient.builder().authenticationProvider( authProvider ).buildClient();

        // Attachment attachment = graphClient.me().messages("AAMkAGUzY5QKjAAA=").attachments("AAMkAGUzY5QKjAAABEgAQAMkpJI_X-LBFgvrv1PlZYd8=")
        //     .buildRequest()
        //     .get();

        //GET https://graph.microsoft.com/v1.0/me/messages/AAMkAGUzY5QKjAAA=/attachments/AAMkAGUzY5QKjAAABEgAQAMkpJI_X-LBFgvrv1PlZYd8=/$value

        // try (BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream());
        //     FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME)) {
        //     byte dataBuffer[] = new byte[1024];
        //     int bytesRead;
        //     while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        //         fileOutputStream.write(dataBuffer, 0, bytesRead);
        //     }
        // } catch (IOException e) {
        //     // handle exception
        // }
        
        //Get our IT group
        // Option filterOption = new QueryOption("$filter", "displayName eq 'IT'");
        // IGroupCollectionPage groups = graphClient
        // 		.groups()
        // 		.buildRequest(Arrays.asList(filterOption))
        // 		.get();
        // Group itGroup = groups.getCurrentPage().get(0);

        return new ResponseEntity<Object>(student, HttpStatus.OK);
    }
}
