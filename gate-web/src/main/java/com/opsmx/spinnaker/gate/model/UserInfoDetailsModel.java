/*
 * Copyright 2021 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opsmx.spinnaker.gate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;

@Data
@JsonInclude
public class UserInfoDetailsModel {

  public String userName;
  public String firstName;
  public String lastName;
  public String userMailId;

  public Collection<String> userRoles = new ArrayList<>();
  public Collection<String> cloudAccounts = new ArrayList<>();
}
