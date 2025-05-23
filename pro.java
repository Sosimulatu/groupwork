
case 5: 
                    System.out.print("Enter positive number: ");
                    num = scanner.nextDouble();
                    if (num > 0) {
                        System.out.println("log(" + num + ") = " + Math.log10(num));
                    } else {
                        System.out.println("Invalid input! Number must be positive.");
                    }
                    break;