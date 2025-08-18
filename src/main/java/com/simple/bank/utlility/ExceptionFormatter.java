package com.simple.bank.utlility;

import com.mysql.cj.exceptions.AssertionFailedException;
import org.springframework.core.NestedRuntimeException;

import java.sql.SQLException;

public class ExceptionFormatter {

    private ExceptionFormatter(){
        throw new AssertionFailedException("utility not allow to instance");
    }

    public static String format(Exception e) {
        if (e == null) {
            return "exception is null";
        }

        Throwable rootCause = getRootCause(e);
        String detailMessage;
        if (rootCause instanceof SQLException) {
            SQLException sqlEx = (SQLException) rootCause;
            int errorCode = sqlEx.getErrorCode();
            detailMessage = "ERROR " + sqlEx.getErrorCode() + ": " + sqlEx.getMessage();
        } else {
            detailMessage = rootCause.getMessage() != null
                    ? rootCause.getMessage()
                    : rootCause.getClass().getSimpleName() + " (no message)";
        }
        return detailMessage;
    }

    private static Throwable getRootCause(Exception e) {
        if (e instanceof NestedRuntimeException) {
            // Spring provide getRootCause()方法
            return ((NestedRuntimeException) e).getRootCause();
        } else {
            // Other Exception: 通过递归getCause()获取根本原因
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            return rootCause;
        }
    }
}

