USE [RIF40]
GO
/****** Object:  Table [dbo].[RIF40_VERSION]    Script Date: 19/09/2014 12:07:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RIF40_VERSION](
	[VERSION] [varchar](50) NOT NULL,
	[SCHEMA_CREATED] [datetime2](0) NOT NULL,
	[SCHEMA_AMENDED] [datetime2](0) NULL,
	[CVS_REVISION] [varchar](50) NOT NULL,
	[ROWID] [uniqueidentifier] NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [dbo].[RIF40_VERSION] ADD  DEFAULT (sysdatetime()) FOR [SCHEMA_CREATED]
GO
ALTER TABLE [dbo].[RIF40_VERSION] ADD  DEFAULT (newid()) FOR [ROWID]
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_VERSION.VERSION' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_VERSION', @level2type=N'COLUMN',@level2name=N'VERSION'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_VERSION.SCHEMA_CREATED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_VERSION', @level2type=N'COLUMN',@level2name=N'SCHEMA_CREATED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_VERSION.SCHEMA_AMENDED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_VERSION', @level2type=N'COLUMN',@level2name=N'SCHEMA_AMENDED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_VERSION.CVS_REVISION' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_VERSION', @level2type=N'COLUMN',@level2name=N'CVS_REVISION'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_VERSION' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_VERSION'
GO
