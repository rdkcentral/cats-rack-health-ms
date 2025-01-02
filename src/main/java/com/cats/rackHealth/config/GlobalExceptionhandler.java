package com.cats.rackHealth.config;

/*
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;


@ControllerAdvice
@RestController
public class GlobalExceptionhandler
{

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );


    @ResponseStatus( code = HttpStatus.BAD_REQUEST )
    @ExceptionHandler( value = IllegalArgumentException.class )
    public String handleIllegalArgumentException( IllegalArgumentException e )
    {
        e.printStackTrace();
        logger.warn( "Exception Caught : " + e.getMessage() );
        return e.getMessage();
    }

    @ResponseStatus( code = HttpStatus.PRECONDITION_FAILED )
    @ExceptionHandler( value = IllegalStateException.class )
    public String handleIllegalStateException( IllegalStateException e )
    {
        e.printStackTrace();
        logger.warn( "Exception Caught : " + e.getMessage() );
        return e.getMessage();
    }

    @ResponseStatus( code = HttpStatus.NOT_ACCEPTABLE )
    @ExceptionHandler( HttpMediaTypeNotAcceptableException.class )
    public String handleHttpMediaTypeNotAcceptableException( HttpMediaTypeNotAcceptableException e )
    {
        logger.warn( "*** Not acceptable exception caught : " + e.getMessage() );
        return e.getMessage();
    }

    @ResponseStatus( code = HttpStatus.BAD_REQUEST )
    @ExceptionHandler( value = HttpClientErrorException.class )
    public String handleHttpClientErrorException( HttpClientErrorException e )
    {
        logger.warn( "HttpClientErrorException Caught :"+ e.getResponseBodyAsString() );
        return e.getResponseBodyAsString();
    }
        
    @ResponseStatus( code = HttpStatus.INTERNAL_SERVER_ERROR )
    @ExceptionHandler( value = Exception.class )
    public String handleException( Exception e )
    {
        e.printStackTrace();
        logger.warn( "Exception Caught : " + e.getMessage() );
        String moreInfo = "";
        StackTraceElement[] stackTrace = e.getStackTrace();
        for ( StackTraceElement trace : stackTrace )
        {
            if ( trace.getClassName().startsWith( "com.cats" ) )
            {
                moreInfo = "\nClass " + trace.getClassName() + "\nMethod: " + trace.getMethodName() + "\nLine:"
                        + trace.getLineNumber();
                break;
            }
        }
        return e.getClass() + "  " + e.getMessage() + "  " + moreInfo;
    }
}
