USE [HR_Management]
GO

INSERT INTO [dbo].[users]
           ([id]
           ,[username]
           ,[password_hash]
           ,[email]
           ,[reset_password_token]
           ,[reset_password_expires]
           ,[created_at]
           ,[updated_at]
           ,[is_active]
           ,[deleted_at])
     VALUES
           (<id, uniqueidentifier,>
           ,<username, varchar(50),>
           ,<password_hash, varchar(255),>
           ,<email, varchar(100),>
           ,<reset_password_token, varchar(255),>
           ,<reset_password_expires, datetime2(7),>
           ,<created_at, datetime2(7),>
           ,<updated_at, datetime2(7),>
           ,<is_active, bit,>
           ,<deleted_at, datetime,>)
GO

